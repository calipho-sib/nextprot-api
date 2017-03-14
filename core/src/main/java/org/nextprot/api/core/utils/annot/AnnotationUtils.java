package org.nextprot.api.core.utils.annot;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.constants.PropertyApiModel;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.BioObject;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.EntryUtils;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;
import org.nextprot.api.core.utils.annot.comp.AnnotationComparators;
import org.nextprot.api.core.utils.annot.merge.impl.AnnotationListMapReduceMerger;
import org.nextprot.api.core.utils.annot.merge.impl.AnnotationListMergerImpl;
import org.nextprot.api.core.utils.graph.OntologyDAG;
import org.nextprot.commons.constants.QualityQualifier;

import java.util.*;
import java.util.stream.Collectors;


public class AnnotationUtils {

	private AnnotationUtils() {
		throw new IllegalAccessError("Utility class");
	}

	public static String toString(Annotation a) {
		StringBuilder sb = new StringBuilder();
		String sep = "\n";
		sb.append("isProteoformAnnotation      :").append(a.isProteoformAnnotation()).append(sep);
		sb.append("getAnnotationHash           :").append(a.getAnnotationHash()).append(sep);
		sb.append("getAnnotationId             :").append(a.getAnnotationId()).append(sep);
		sb.append("getAnnotationName           :").append(a.getAnnotationName()).append(sep);
		sb.append("getUniqueName               :").append(a.getUniqueName()).append(sep);
	//	sb.append("getSubjectName              :").append(a.getSubjectName()).append(sep);

		sb.append("getDescription              :").append(a.getDescription()).append(sep);
		sb.append("getBioObject                :").append(a.getBioObject()==null ? "null" : a.getBioObject()).append(sep);
		
		sb.append("getSubjectComponents size   :").append(a.getSubjectComponents()==null ? 0 : a.getSubjectComponents().size()).append(sep);
		if (a.getSubjectComponents()!=null) {
			for (String c: a.getSubjectComponents()) 
				sb.append("- component                 :").append(c).append(sep);
		}
		sb.append("getApiTypeName              :").append(a.getApiTypeName()).append(sep);
		sb.append("getCategory                 :").append(a.getCategory()).append(sep);
		sb.append("getCategoryName             :").append(a.getCategoryName()).append(sep);
		sb.append("getCvTermAccessionCode      :").append(a.getCvTermAccessionCode()).append(sep);
		sb.append("getCvTermName               :").append(a.getCvTermName()).append(sep);
		sb.append("getEvidences.size           :").append(a.getEvidences()==null ? 0:a.getEvidences().size()).append(sep);
		if (a.getEvidences()!=null) {
			for (AnnotationEvidence ae: a.getEvidences()) 
				sb.append("- evidence                 :").append(ae.getEvidenceId()).append(sep);
		}
		sb.append("getTargetingIsoformsMap size:").append(a.getTargetingIsoformsMap()==null ? 0:a.getTargetingIsoformsMap().size()).append(sep);
		//sb.append("").append(a.).append(sep);
		return sb.toString();
	}

	
    /**
	 * Filter annotation by its hashes
	 */
	public static List<Annotation> filterAnnotationsByHashes(Entry entry, Set<String> hashes) {

        List<Annotation> annotations = entry.getAnnotations();
		if (annotations == null) return null;

		return annotations.stream()
				.filter(a -> hashes.contains(a.getAnnotationHash()))
				.collect(Collectors.toList());
        
	}
	
	
    /**
	 * Filter annotation by its category
	 * WARNING: goldOnly if set to true will change evidences of the annotations (remove any silver evidence if set to true)
	 */
	public static List<Annotation> filterAnnotationsByCategory(Entry entry, AnnotationCategory annotationCategory, boolean goldOnly) {

        return filterAnnotationsByCategory(entry, annotationCategory, true, goldOnly);
	}

    /**
	 * Filter annotation of the entry by its category
	 * WARNING: goldOnly if set to true will change evidences of the annotations (remove any silver evidence if set to true)
	 */
	public static List<Annotation> filterAnnotationsByCategory(Entry entry, AnnotationCategory annotationCategory, boolean withChildren, boolean goldOnly) {
		return filterAnnotationsByCategory(entry.getAnnotations(), annotationCategory, withChildren, goldOnly);
	}

	/**
	 * Filter annotations where cvterm is a descendant of the given ancestor
	 *
	 * @param annotations the annotation list to filter
	 * @param dag the ontology graph
	 * @param ancestor the ancestor node
	 * @return the filtered list
	 */
	public static List<Annotation> filterAnnotationsByCvTermDescendingFromAncestor(List<Annotation> annotations, OntologyDAG dag, CvTerm ancestor) {

		if (annotations == null)
			return new ArrayList<>();

		if (!dag.hasCvTermAccession(ancestor.getAccession())) {
			return annotations;
		}

		return annotations.stream()
				.filter(annotation -> {
					try {
						return annotation.getCvTermAccessionCode() != null && dag.isAncestorOf(ancestor.getAccession(), annotation.getCvTermAccessionCode());
					} catch (OntologyDAG.NotFoundNodeException e) {
						return false;
					}
				}).collect(Collectors.toList());
	}

	/**
	 * Filter annotation by its category
	 * @param withChildren if true, annotations having a category which is a child of annotationCategory are included in the list
	 * @return a list of annotations
	 */
	public static List<Annotation> filterAnnotationsByCategory(List<Annotation> annotations, AnnotationCategory annotationCategory, boolean withChildren, boolean goldOnly) {

		if (annotations == null) return null;

		List<Annotation> filteredAnnotations = annotations.stream()
				.filter((a) -> {
					boolean categoryMatch = (annotationCategory == null) || ((a.getAPICategory() == annotationCategory) || (withChildren && a.getAPICategory().isChildOf(annotationCategory)));
					boolean qualityMatch = true;
					if(goldOnly){
						qualityMatch = "GOLD".equalsIgnoreCase(a.getQualityQualifier());
					}
					return categoryMatch && qualityMatch;
				}).collect(Collectors.toList());
		
		if (goldOnly) {
			for(Annotation a : filteredAnnotations) {
				List<AnnotationEvidence> evidences = a.getEvidences();

				List<AnnotationEvidence> goldEvidences = evidences.stream()
						.filter(e -> "GOLD".equalsIgnoreCase(e.getQualityQualifier()) || (e.getQualityQualifier() == null) || e.getQualityQualifier().isEmpty())
						.collect(Collectors.toList());
				
				 //TODO check if this mutable annotation is not breaken in eh cache!!
				a.setEvidences(goldEvidences);
			}
		}

		if (annotationCategory == AnnotationCategory.PHENOTYPIC_VARIATION) {
			Collections.sort(filteredAnnotations, AnnotationComparators.newPhenotypicVariationComparator(EntryUtils.getHashAnnotationMap(annotations)));
		} else {
			Collections.sort(filteredAnnotations, AnnotationComparators.newComparator(annotationCategory));
		}

		return filteredAnnotations;
	}

	public static Set<Long> getExperimentalContextIdsForAnnotations(List<Annotation> annotations) {
		Set<Long> ecIds = new HashSet<>();
		for(Annotation a : annotations){
			for(AnnotationEvidence e : a.getEvidences()) {
				Long ecId = e.getExperimentalContextId();
				if(ecId!=null) ecIds.add(ecId);
			}
		}
		return ecIds;		
	}
	

	
	public static List<Annotation> filterAnnotationsBetweenPositions(int start, int end, List<Annotation> annotations, String isoform) {
		if(annotations == null) return null;
		List<Annotation> finalAnnotations = new ArrayList<>();
		for (Annotation annot : annotations) {
			if (annot.isAnnotationPositionalForIsoform(isoform)) {
				int isoStartPosition, isoEndPosition;
				isoStartPosition = annot.getStartPositionForIsoform(isoform);
				isoEndPosition = annot.getEndPositionForIsoform(isoform);
				if ((isoStartPosition >= start) && (isoEndPosition <= end)) {
					finalAnnotations.add(annot);
				}
			}
		}
		return finalAnnotations;
	}
	
	public static Set<Long> getXrefIdsForAnnotations(List<Annotation> annotations){
		if(annotations == null) return null;
		Set<Long> xrefIds = new HashSet<>();
		for(Annotation a : annotations){
			for(AnnotationEvidence e : a.getEvidences()){
				if(e.isResourceAXref()){
					xrefIds.add(e.getResourceId());
				}
			}
		}
		return xrefIds;
	}

	/*
	 * Returns a set of xref identifiers in some special cases:
	 * - "sequence caution" annotation type => xrefs found in "differing sequence" property
	 * - "cofactor" annotation type => xrefs found in "cofactor" property
	 */
	public static Set<Long> getXrefIdsFromAnnotations(List<Annotation> annotations){
		Set<Long> xrefIds = new HashSet<>();

		for(Annotation a : annotations){
			if (a.getAPICategory()== AnnotationCategory.SEQUENCE_CAUTION) {
				addXrefIdRelatedToAnnotationPropertyName(a, PropertyApiModel.NAME_DIFFERING_SEQUENCE, xrefIds);
			}
			else if (a.getAPICategory()== AnnotationCategory.COFACTOR) {
				xrefIds.add(a.getBioObject().getId());
			}
			else if (a.getAPICategory()== AnnotationCategory.DISEASE) {
				addXrefIdRelatedToAnnotationPropertyName(a, PropertyApiModel.NAME_ALTERNATIVE_DISEASE_TERM, xrefIds);
			}
		}
		return xrefIds;
	}

	private static void addXrefIdRelatedToAnnotationPropertyName(Annotation a, String propName, Set<Long> xrefIds) {
		for (AnnotationProperty p: a.getProperties()) {
			if (p.getName().equals(propName) && p.getValueType().equals(PropertyApiModel.VALUE_TYPE_RIF)) {
				xrefIds.add(Long.parseLong(p.getValue()));
			}
		}		
	}

	/*
	 * Returns a set of xref identifiers corresponding to the interactants which are involved 
	 * in binary interaction annotations and which are not human proteins (xeno interactions)
	 */
	public static Set<Long> getXrefIdsForInteractionsInteractants(List<Annotation> annotations){
		if(annotations == null) return null;
		Set<Long> xrefIds = new HashSet<>();
		for(Annotation a : annotations){
			if (a.getAPICategory()== AnnotationCategory.BINARY_INTERACTION) {
				for (AnnotationProperty p: a.getProperties()) {
					if (p.getName().equals(PropertyApiModel.NAME_INTERACTANT) && p.getValueType().equals(PropertyApiModel.VALUE_TYPE_RIF)) {
						xrefIds.add(Long.parseLong(p.getValue()));
					}
				}
			}
		}
		return xrefIds;
	}


	public static Set<Long> getPublicationIdsForAnnotations(List<Annotation> annotations) {

		Set<Long> publicationIds = new HashSet<>();
		for(Annotation a : annotations){
			for(AnnotationEvidence e : a.getEvidences()){
				if(e.isResourceAPublication()){
					publicationIds.add(e.getResourceId());
				}
			}
		}
		return publicationIds;
	
	}
	
	/*
	 * SEQUENCE_CAUTION => property name = differing sequence
	 * COFACTOR         => property name = cofactor
	 * DISEASE          => property name = alternative disease term
	 */
	public static void convertRelativeEvidencesToProperties(List<Annotation> annotations) {
		for (Annotation annot: annotations) {

			List<AnnotationEvidence> evidencesToRemove = null;

			if (annot.getAPICategory()== AnnotationCategory.SEQUENCE_CAUTION) {
				evidencesToRemove = convertRelativeEvidenceToProperty(annot, PropertyApiModel.NAME_DIFFERING_SEQUENCE);
			} 
			else if (annot.getAPICategory()== AnnotationCategory.COFACTOR) {
				evidencesToRemove = convertRelativeEvidenceToExternalChemicalBioObject(annot);
			}
			else if (annot.getAPICategory()== AnnotationCategory.DISEASE) {
				evidencesToRemove = convertRelativeEvidenceToProperty(annot, PropertyApiModel.NAME_ALTERNATIVE_DISEASE_TERM);
			}

			if (evidencesToRemove != null)
				annot.getEvidences().removeAll(evidencesToRemove);
		}
	}

	private static List<AnnotationEvidence> convertRelativeEvidenceToProperty(Annotation annot, String propertyName) {

		List<AnnotationEvidence> toRemove = new ArrayList<>();

		for (AnnotationEvidence evi : annot.getEvidences()) {
			if ("relative".equals(evi.getResourceAssociationType())) {

				AnnotationProperty p = new AnnotationProperty();

				p.setAnnotationId(annot.getAnnotationId());
				p.setAccession(evi.getResourceAccession());
				p.setName(propertyName);
				p.setValue(Long.toString(evi.getResourceId()));
				p.setValueType(PropertyApiModel.VALUE_TYPE_RIF);
				annot.addProperties(Arrays.asList(p));

				toRemove.add(evi);
			}
		}

		return toRemove;
	}

	private static List<AnnotationEvidence> convertRelativeEvidenceToExternalChemicalBioObject(Annotation annot) {

		List<AnnotationEvidence> toRemove = new ArrayList<>();

		for (AnnotationEvidence evi : annot.getEvidences()) {

			if ("relative".equals(evi.getResourceAssociationType())) {

				annot.setBioObject(newExternalChemicalBioObject(evi));

				toRemove.add(evi);
			}
		}

		return toRemove;
	}

	static BioObject newExternalChemicalBioObject(AnnotationEvidence evi) {

		BioObject bo = BioObject.external(BioObject.BioType.CHEMICAL, evi.getResourceDb());
		bo.setId(evi.getResourceId());
		bo.setAccession(evi.getResourceAccession());
		String chemicalName = evi.getPropertyValue("name");
		if (chemicalName!=null) bo.getProperties().put("chemical name", chemicalName);
		return bo;
	}

	public static List<Annotation> merge(List<Annotation> srcAnnotationList, List<Annotation> destAnnotationList) {

		return new AnnotationListMergerImpl().merge(srcAnnotationList, destAnnotationList);
	}

	public static List<Annotation> mapReduceMerge(List<Annotation> statementAnnotations, List<Annotation> standardAnnotations) {

		return new AnnotationListMapReduceMerger().merge(statementAnnotations, standardAnnotations);
	}

	public static QualityQualifier computeAnnotationQualityBasedOnEvidences(List<AnnotationEvidence> evidences) {

		if(evidences == null || evidences.isEmpty()){
			throw new NextProtException("Can't compute quality qualifier based on empty / null evidences");
		}

		for(AnnotationEvidence e : evidences){
			if(e.getQualityQualifier() == null){
				throw new NextProtException("Found evidence without any quality");
			}
			
			QualityQualifier q = QualityQualifier.valueOf(e.getQualityQualifier());
			if(q.equals(QualityQualifier.GOLD)) //If one evidence is GOLD return GOLD
				return QualityQualifier.GOLD;
			
		}
		
		return QualityQualifier.SILVER;
	}
}
