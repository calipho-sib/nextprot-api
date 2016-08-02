package org.nextprot.api.core.utils;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.constants.PropertyApiModel;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.BioObject;
import org.nextprot.api.core.domain.BioObjectExternal;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;
import org.nextprot.api.core.domain.annotation.IsoformAnnotation;
import org.nextprot.api.core.utils.annot.AnnotationFinder;
import org.nextprot.api.core.utils.annot.AnnotationMergeImpl;
import org.nextprot.api.core.utils.annot.AnnotationMerger;
import org.nextprot.commons.constants.QualityQualifier;

import java.util.*;
import java.util.stream.Collectors;


public class AnnotationUtils {

	private static final AnnotationPropertyComparator ANNOTATION_PROPERTY_COMPARATOR = new AnnotationPropertyComparator();


	/**
	 * This method filters annotations by gold only.
	 * WARNIIIIIIIIIIIIIIIIIIIIINGGGGGGGGGGGGGGGGGGGGGGGGGGGG
	 * Careful this method modifies the annotations object!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 */
	public static List<IsoformAnnotation> filterAnnotationsByGoldOnlyCarefulThisChangesAnnotations(List<IsoformAnnotation > annotations, Boolean goldOnly) {

		if((goldOnly == null) ? false : Boolean.valueOf(goldOnly)){
			List<IsoformAnnotation> goldOnlyAnnotations = new ArrayList<IsoformAnnotation>();
			for(IsoformAnnotation a : annotations){
				List<AnnotationEvidence> goldOnlyEvidences = a.getEvidences().stream().filter(e -> ((e.getQualityQualifier() == null) || e.getQualityQualifier().toLowerCase().equals("gold"))).
						collect(Collectors.toList());
				if(!goldOnlyEvidences.isEmpty()){
					a.setEvidences(goldOnlyEvidences); ////////////// CAREFUL ANNOTATIONS ARE MODIFIED!!!!!!!!!!!!Should use immutable objects
					goldOnlyAnnotations.add(a);
				}
			}
			return goldOnlyAnnotations;
		}else return annotations;
	}
	
	
    /**
	 * Filter annotation by its category
	 */
	public static List<Annotation> filterAnnotationsByCategory(Entry entry, AnnotationCategory annotationCategory) {

        return filterAnnotationsByCategory(entry, annotationCategory, true);
	}
	
	/**
	 * Filter annotation by its category
	 * @param withChildren if true, annotations having a category which is a child of annotationCategory are included in the list
	 * @return a list of annotations
	 */
	public static List<Annotation> filterAnnotationsByCategory(Entry entry, AnnotationCategory annotationCategory, boolean withChildren) {

        Isoform canonicalIsoform = IsoformUtils.getCanonicalIsoform(entry);

        List<Annotation> annotations = entry.getAnnotations();

		if (annotations == null) return null;

        List<Annotation> annotationList = new ArrayList<>();

        for (Annotation a : annotations){

            if (a.getAPICategory() != null) {

                if (a.getAPICategory().equals(annotationCategory)) {
					annotationList.add(a);
				}
                else if (withChildren && a.getAPICategory().isChildOf(annotationCategory) ) {
					annotationList.add(a);
				}
			}
		}

        if (canonicalIsoform != null) {
            Collections.sort(annotationList, new AnnotationComparator(canonicalIsoform));
        }

		return annotationList;
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
			if (p.getName().equals(propName)) {
				if (p.getValueType().equals(PropertyApiModel.VALUE_TYPE_RIF)) {
					xrefIds.add(Long.parseLong(p.getValue()));
				}
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
					if (p.getName().equals(PropertyApiModel.NAME_INTERACTANT)) {
						if (p.getValueType().equals(PropertyApiModel.VALUE_TYPE_RIF)) xrefIds.add(Long.parseLong(p.getValue()));
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
	public static void convertType2EvidencesToProperties(List<Annotation> annotations) {
		for (Annotation annot: annotations) {

			List<AnnotationEvidence> evidencesToRemove = null;

			if (annot.getAPICategory()== AnnotationCategory.SEQUENCE_CAUTION) {
				evidencesToRemove = convertType2EvidenceToProperty(annot, PropertyApiModel.NAME_DIFFERING_SEQUENCE);
			} 
			else if (annot.getAPICategory()== AnnotationCategory.COFACTOR) {
				evidencesToRemove = convertEvidenceToExternalBioObject(annot);
			}
			else if (annot.getAPICategory()== AnnotationCategory.DISEASE) {
				evidencesToRemove = convertType2EvidenceToProperty(annot, PropertyApiModel.NAME_ALTERNATIVE_DISEASE_TERM);
			}

			if (evidencesToRemove != null)
				annot.getEvidences().removeAll(evidencesToRemove);
		}
	}

	private static List<AnnotationEvidence> convertType2EvidenceToProperty(Annotation annot, String propertyName) {

		List<AnnotationEvidence> toRemove = new ArrayList<>();

		for (AnnotationEvidence evi : annot.getEvidences()) {
			if (evi.getResourceAssociationType().equals("relative")) {

				AnnotationProperty p = new AnnotationProperty();

				p.setAnnotationId(annot.getAnnotationId());
				p.setAccession(evi.getResourceAccession());
				p.setName(propertyName);
				p.setValue(""+evi.getResourceId());
				p.setValueType(PropertyApiModel.VALUE_TYPE_RIF);
				annot.addProperties(Arrays.asList(p));

				toRemove.add(evi);
			}
		}

		return toRemove;
	}

	private static List<AnnotationEvidence> convertEvidenceToExternalBioObject(Annotation annot) {

		List<AnnotationEvidence> toRemove = new ArrayList<>();

		for (AnnotationEvidence evi : annot.getEvidences()) {

			if (evi.getResourceAssociationType().equals("relative")) {

				annot.setBioObject(newExternalBioObject(evi));

				toRemove.add(evi);
			}
		}

		return toRemove;
	}

	static BioObject newExternalBioObject(AnnotationEvidence evi) {

		BioObject bo = new BioObjectExternal(BioObject.BioType.CHEMICAL, evi.getResourceDb());

		bo.setId(evi.getResourceId());
		bo.setAccession(evi.getResourceAccession());

		return bo;
	}

	public static AnnotationPropertyComparator getInstanceOfAnnotationPropertyComparator() {

		return ANNOTATION_PROPERTY_COMPARATOR;
	}


	//If You find the annotation on NP1 (annotations) add the evidence (SIMPLE CASE)
	//If You don't find the annotation on NP1 (annotations) add the new annotation to the list + SET
	//If one evidence contains GOLD the annotation is GOLD. (maybe from BioEditor we get variants that are GOLD and in NP1 we have silver ones) so annotation will become GOLD
	// GO should be easy (go-cellular-component, go-molecular-function, go-biological-process)
	// variant and mutagenesis should be a bit more complicated but doable (look at origin / positions ... )
	// binary-interaction 66 (may require some adaptation from AnnotationBuilder)
	//small-molecule-interaction	4 (may require some adaptation from AnnotationBuilder, new XREF)
	//Set the correct annotation id to the evidence (AnnotationEvidence.setAnnotationId....)
	//Set the correct subject componenents if it is in NP1

	/**
	 * Merge source annotations into destination annotations (update destination annotations if needed)
	 * see specs https://issues.isb-sib.ch/browse/BIOEDITOR-454
	 * @param srcAnnotationList
	 * @param destAnnotationList
     */
	public static void merge(List<Annotation> srcAnnotationList, List<Annotation> destAnnotationList) {

		AnnotationMerger merger = new AnnotationMergeImpl();

		for (Annotation srcAnnotation : srcAnnotationList) {

			AnnotationFinder finder = AnnotationFinder.newFinder(srcAnnotation.getCategory());
			Annotation foundAnnotation = finder.find(srcAnnotation, destAnnotationList);

			// not found -> add new annotation
			if (foundAnnotation == null) {

				destAnnotationList.add(srcAnnotation);
			}
			// found -> update annotation with statementAnnotation
			else {

				merger.update(foundAnnotation, srcAnnotation);
			}
		}
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
