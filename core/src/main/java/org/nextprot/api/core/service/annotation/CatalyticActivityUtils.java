package org.nextprot.api.core.service.annotation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.constants.IdentifierOffset;
import org.nextprot.api.commons.constants.Xref2Annotation;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;


public class CatalyticActivityUtils {

	private CatalyticActivityUtils() {
		throw new IllegalAccessError("Utility class");
	}

	
	/**
	 * @param catalyticAnnot a "catalytic activity" annotation
	 * @return a list of "small molecule interaction" annotations derived from @param parentAnnot
	 */
	public static List<Annotation> createSMIAnnotations(String entryName, List<Isoform> isoforms, Annotation catalyticAnnot, List<DbXref> entryXrefs) {

		final String REACTION_PROP_NAME = "reaction";
		final String PARTICIPANT_PROP_NAME = "participant";
		
		List<Annotation> smiAnnotations = new ArrayList<>();
		
		// makes sense for catalytic activity annotations only
		if (AnnotationCategory.CATALYTIC_ACTIVITY != catalyticAnnot.getAPICategory()) return smiAnnotations;
		
		// annotation evidence will be based on RHEA xref found from reaction property
		if (catalyticAnnot.getPropertiesByKey(REACTION_PROP_NAME) == null) return smiAnnotations;
		if (catalyticAnnot.getPropertiesByKey(REACTION_PROP_NAME).isEmpty()) return smiAnnotations;
		String reactionStr = catalyticAnnot.getPropertiesByKey(REACTION_PROP_NAME).iterator().next().getValue();
		DbXref reaXref = findXrefFromProperty(REACTION_PROP_NAME, reactionStr, entryXrefs);
		if (reaXref == null) return smiAnnotations;

		// annotation bio object will be based on CHEBI xref found from participant property
		if (catalyticAnnot.getPropertiesByKey(PARTICIPANT_PROP_NAME) == null) return smiAnnotations;
		if (catalyticAnnot.getPropertiesByKey(PARTICIPANT_PROP_NAME).isEmpty()) return smiAnnotations;
		
		// now create small molecule annotations from ChEBI xref and RheaXref
		for (AnnotationProperty partiProp : catalyticAnnot.getPropertiesByKey(PARTICIPANT_PROP_NAME) ) {
			String partiStr = partiProp.getValue();
			DbXref partiXref = findXrefFromProperty(PARTICIPANT_PROP_NAME, partiStr, entryXrefs); 
			if (partiXref==null) continue;
			Annotation smiAnnot = newSmiAnnotation(entryName, isoforms, catalyticAnnot, partiXref,reaXref);
			smiAnnotations.add(smiAnnot);
		}
		return smiAnnotations;
	}
	
	public static Annotation newSmiAnnotation(String entryName, List<Isoform> isoforms, Annotation parentAnnot, DbXref participantXref, DbXref reactionXref) {
		Annotation annotation = new Annotation();
		annotation.setAnnotationId(IdentifierOffset.SMI_ANNOTATION_ID_COUNTER.incrementAndGet());
		annotation.setAnnotationCategory(AnnotationCategory.SMALL_MOLECULE_INTERACTION);
		annotation.setDescription(null); 
		annotation.setQualityQualifier(parentAnnot.getQualityQualifier()); 
		annotation.setCvTermName(null);
		annotation.setCvTermAccessionCode(null);
		annotation.setSynonym(null);
		annotation.setUniqueName("AN_" + entryName.substring(3) + "_SMI_" + annotation.getAnnotationId());
		annotation.setParentXref(null);
		annotation.setBioObject(AnnotationUtils.newExternalChemicalBioObject(participantXref, "name"));
		annotation.addTargetingIsoforms(AnnotationUtils.newNonPositionalAnnotationIsoformSpecificityList(isoforms, annotation));

		AnnotationEvidence evidence = new AnnotationEvidence();
		evidence.setAnnotationId(annotation.getAnnotationId());
		evidence.setEvidenceId(IdentifierOffset.SMI_EVIDENCE_ID_COUNTER.incrementAndGet());
		evidence.setAssignedBy("Uniprot");
		evidence.setResourceId(reactionXref.getDbXrefId());
		evidence.setResourceAccession(reactionXref.getAccession());
		evidence.setResourceDb(reactionXref.getDatabaseName());
		evidence.setResourceAssociationType("evidence");
		evidence.setResourceType("database");
		evidence.setNegativeEvidence(false);
		evidence.setExperimentalContextId(null);
		evidence.setResourceDescription(null);
		evidence.setProperties(new ArrayList<>());
		evidence.setQualifierType("IC");
		evidence.setQualityQualifier(parentAnnot.getQualityQualifier());
		evidence.setAssignmentMethod("curated");
		evidence.setEvidenceCodeOntology("EvidenceCodeOntologyCv");
		evidence.setEvidenceCodeAC("ECO:0000364");							
		evidence.setEvidenceCodeName("evidence based on logical inference from manual annotation used in automatic assertion");									// TODO		
		
		List<AnnotationEvidence> evidences = new ArrayList<>();
		evidences.add(evidence);
		annotation.setEvidences(evidences);		
		return annotation;
	}
	
	
	/**
	 * Returns the xref in xrefs that have the same ac  as the one in the property
	 * of a catalytic annotation
	 * @param propName the property name: participant or reaction, error otherwise
	 * @param propValue the property value
	 * @param xrefs the list of xrefs (i.e. entry xrefs) to search
	 * @return the xref with the same ac as in the property value or null
	 */
	public static DbXref findXrefFromProperty(String propName, String propValue, List<DbXref> xrefs) {
		
		// format of the property value "some_label<space>[<ac>]+
		
		String db = "participant".equals(propName) ? "CHEBI" : ("reaction".equals(propName) ? "RHEA" : "unexpected");
		if ("unexpected".equals(db)) throw new NextProtException("Unexpected catalytic activity property name:" + propName);

		int pos1 = propValue.indexOf(db+":");
		int pos2 = propValue.indexOf("]");
		String ac = propValue.substring(pos1,pos2);
		for (DbXref x : xrefs ) {
			if (x.getAccession().equals(ac)) return x;
		}

		// log some warning because to get here is not supposed to occur but no logger easy to implement in this static so:
		System.out.println("WARNING: Could not find xref from property: " + propName + "=" + propValue);		
		return null;
	}
		
	/**
	 * We assume we get SMI annotations about the same protein
	 * The merging process in useful in this case:
	 * We may have a ChEBI involved in N (more than 1) catalytic activities of the protein
	 * which yield N SMI annotations to be built
	 * In this case we want to keep a single annotation for the ChEBI and add to this single
	 * annotation the evidences existing in the equivalent annotations which will be discarded:
	 * Example of merging process:
	 * SMI-001: 1 SMI annot for ChEBI:12345 with 1 evidence based on RHEA:11111
	 * SMI-002: 1 SMI annot for ChEBI:12345 with 1 evidence based on RHEA:22222
	 * will be turned into:
	 * SMI-001: 1 SMI annot for ChEBI:12345 with 2 evidences (1 based on RHEA:11111, 1 on RHEA:22222)
	 * 
	 * @param smiAnnotations a list of SMI annotations of an entry
	 * @return a list of merged SMI annotations of the entry 
	 */
	public static List<Annotation> mergeSmiAnnotations(List<Annotation> smiAnnotations) {
		
		List<Annotation> mergedAnnotations = new ArrayList<>();
		Map<String,Annotation> chebiMergedAnnot = new HashMap<>();
		for (Annotation annot: smiAnnotations) {
			String chebi = annot.getBioObject().getAccession();
			if (chebiMergedAnnot.containsKey(chebi)) {
				// we already have an annotation for this ChEBI (mergedAnnotation below)
				Annotation mergedAnnot = chebiMergedAnnot.get(chebi);
				// we extract the evidence of the current annotation and attach it to the merged annotation
				AnnotationEvidence evi = annot.getEvidences().get(0);
				evi.setAnnotationId(mergedAnnot.getAnnotationId());
				mergedAnnot.getEvidences().add(evi);
			} else {
				// we have not yet an annotation for this chebi we add it in the map of mergedAnnotation
				chebiMergedAnnot.put(chebi, annot);
				// we add it in the final list as well: the fist annotation for a given ChEBI 
				// becomes the merged annotation 
				mergedAnnotations.add(annot);
			}
		}
		return mergedAnnotations;
	}
	
	
}
