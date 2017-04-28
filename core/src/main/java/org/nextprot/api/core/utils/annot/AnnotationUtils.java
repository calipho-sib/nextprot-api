package org.nextprot.api.core.utils.annot;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.constants.PropertyApiModel;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.BioObject;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.EntryUtils;
import org.nextprot.api.core.domain.ExperimentalContext;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;
import org.nextprot.api.core.utils.annot.comp.AnnotationComparators;
import org.nextprot.api.core.utils.annot.merge.impl.AnnotationListMapReduceMerger;
import org.nextprot.api.core.utils.annot.merge.impl.AnnotationListMergerImpl;
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
			BioObject bo = a.getBioObject();

			if (bo != null && bo.isInteractant()) {
				xrefIds.add(bo.getId());
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
	
	/**
	 * 
	 * @param annot
	 * @return
	 */
	public static String getTermNameWithAncestors(Annotation annot, List<CvTerm> terms) {
		

		StringBuffer sb = new StringBuffer();
		for (int i=0; i<terms.size(); i++) {
			if (i>0) sb.insert(0, " » ");
			sb.insert(0, terms.get(i).getName());
		}
		return sb.toString();
	}
	
	/**
	 * pam, 28 March 2017
	 * This method returns true for variant annotations that are somehow related to a disease
	 * otherwise returns false.
	 * @param annot any annotation
	 */
	public static boolean isVariantRelatedToDiseaseProperty(Annotation annot, Map<Long,ExperimentalContext> ecs) {
		
		if (AnnotationCategory.VARIANT != annot.getAPICategory()) return false;
		
		// condition 1: if there is a disease in an evidence experimental context, return true > 1'000'000 cases
		
		for (AnnotationEvidence ev : annot.getEvidences()) {
			Long ecId = ev.getExperimentalContextId();
			if (ecId != null && ecId != 0) {
				ExperimentalContext ec = ecs.get(ecId);
				if (ec==null) {
					System.out.println("WARNING: Could not find ExperimentalContext with id:" + ecId );
				} else {   
					if (ec.getDisease() != null) return true;
				}
			}
		}
		
		// condition 2: if there exists at least 1 variant disease term, return true > 30'000 cases
		
		if (annot.getVariant()!=null && annot.getVariant().getDiseaseTerms() != null) {
			if (annot.getVariant().getDiseaseTerms().size() > 0) return true;
		}
		
		// condition 3: description matches some patterns, return true > 5'000 cases
		
		if (annot.getDescription()==null) return false;
		
		String desc = annot.getDescription().toLowerCase();
		
		if (desc.contains("allele")) return false;
		if (desc.contains("population")) return false;
		if (desc.contains("isozyme")) return false;
		if (desc.contains("%")) return false;
		if (desc.contains("clone")) return false;
		if (desc.contains("polymorphism")) return false;
		
		if (desc.startsWith("in")) return true;
		if (desc.contains("found in")) return true;
		if (desc.contains("associated with")) return true;
		
		// else  > 3'000'000 cases
		
		return false;
	}
	

	public static boolean isMiscRegionRelatedToInteractions(Annotation annot) {
		
		if (AnnotationCategory.MISCELLANEOUS_REGION != annot.getAPICategory()) return false;
		if (annot.getDescription()==null) return false;
		
		String desc = annot.getDescription().toLowerCase();
		return desc.contains("bind");
	}
	
	
	
	/**
	 * Pam, 22 march 2017
	 * 
	 * This method is created to display properly the general annotations that are isoform specific.
	 * 
	 * 1) The general rule in NP1 is that an annotation is displayed as specific 
	 * if there exists a targetingIsoformMap record for the isoform AND the number of targetingIsoformMap 
	 * records for this annotation is inferior to the number of isoforms (which means that the annotation doesn't apply to each isoform)
	 * 
	 * 2) There is known exception in NP1 for binary interaction annotations. In this case we always have a targetingIsoformMap record 
	 * for each isoform but you must rely on the targetingIsoformMap.getSpecificity() to determine if the annotation 
	 * is specific for an isoform or not. This rule was introduced because we didn't want to penalize IntAct annotations that
	 * are supported by experiments performed with a known isoform.
	 * 
	 * 3) Some annotation categories may require a review concerning how they deal with isoform specificity (NP1 & BED pipelines)
	 * A jira issue will be created...
	 * 
	 * @param annot
	 * @param entryIsoformCount
	 * @return
	 */
	public static List<String> computeIsoformsDisplayedAsSpecific(Annotation annot, int entryIsoformCount) {
		
		List<String> result = new ArrayList<String>();
		if (annot.getTargetingIsoformsMap()==null) return result;
		
		if (AnnotationCategory.BINARY_INTERACTION==annot.getAPICategory()) {
			for (AnnotationIsoformSpecificity spec : annot.getTargetingIsoformsMap().values()) {
				if ("SPECIFIC".equals(spec.getSpecificity())) result.add(spec.getIsoformAccession());
			}
			if (result.size()==entryIsoformCount) result = new ArrayList<String>();
		} else {
			if (annot.getTargetingIsoformsMap().size()<entryIsoformCount) {
				for (AnnotationIsoformSpecificity spec : annot.getTargetingIsoformsMap().values()) {
					result.add(spec.getIsoformAccession());
				}
			}
		}
		return result;
	}

	
}
