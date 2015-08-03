package org.nextprot.api.core.utils;

import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;

import java.util.*;


public class AnnotationUtils {

	private static Comparator<Annotation> COMPARATOR;


	static {
		COMPARATOR = new Comparator<Annotation>() {
			@Override
			public int compare(Annotation a1, Annotation a2) {

				return Long.compare(a1.getAnnotationId(), a2.getAnnotationId());
			}
		};
	}
	
	/**
	 * Filter annotation by its category
	 * @param annotations
	 * @param annotationCategory
	 * @return
	 */
	public static List<Annotation> filterAnnotationsByCategory(List<Annotation> annotations, AnnotationApiModel annotationCategory) {
		return filterAnnotationsByCategory(annotations,  annotationCategory, true);
	}
	
	/**
	 * Filter annotation by its category
	 * @param annotations
	 * @param annotationCategory
	 * @param withChildren if true, annotations having a category which is a child of annotationCategory are included in the list 
	 * @return a list of annotations
	 */
	public static List<Annotation> filterAnnotationsByCategory(List<Annotation> annotations, AnnotationApiModel annotationCategory, boolean withChildren) {
		List<Annotation> annotationList = new ArrayList<Annotation>(); 
		for(Annotation a : annotations){
			if(a.getAPICategory() != null) {
				if (a.getAPICategory().equals(annotationCategory)) {
					annotationList.add(a);
				} else if (withChildren && a.getAPICategory().isChildOf(annotationCategory) ) {
					annotationList.add(a);
				}
			}
		}

		Collections.sort(annotationList, COMPARATOR);

		return annotationList;
	}
	
	
	public static Set<Long> getExperimentalContextIdsForAnnotations(List<Annotation> annotations) {
		Set<Long> ecIds = new HashSet<Long>(); 
		for(Annotation a : annotations){
			for(AnnotationEvidence e : a.getEvidences()) {
				Long ecId = e.getExperimentalContextId();
				if(ecId!=null) ecIds.add(ecId);
			}
		}
		return ecIds;		
	}
	
	
	public static Set<Long> getXrefIdsForAnnotations(List<Annotation> annotations){
		Set<Long> xrefIds = new HashSet<Long>(); 
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
	 * - "cofactor" annotation rype => xrefs found in "cofactor" property
	 * 
	 */
	public static Set<Long> getXrefIdsForAnnotationsProperties(List<Annotation> annotations){
		Set<Long> xrefIds = new HashSet<Long>(); 
		for(Annotation a : annotations){
			if (a.getAPICategory()==AnnotationApiModel.SEQUENCE_CAUTION) {
				addXrefIdRelatedToAnnotationPropertyName(a, AnnotationProperty.NAME_DIFFERING_SEQUENCE, xrefIds);
			}
			else if (a.getAPICategory()==AnnotationApiModel.COFACTOR) {
				addXrefIdRelatedToAnnotationPropertyName(a, AnnotationProperty.NAME_COFACTOR, xrefIds);
			}
			else if (a.getAPICategory()==AnnotationApiModel.DISEASE) {
				addXrefIdRelatedToAnnotationPropertyName(a, AnnotationProperty.NAME_ALTERNATIVE_DISEASE_TERM, xrefIds);
			}
		}
		return xrefIds;
	}

	private static void addXrefIdRelatedToAnnotationPropertyName(Annotation a, String propName, Set<Long> xrefIds) {
		for (AnnotationProperty p: a.getProperties()) {
			if (p.getName().equals(propName)) {
				if (p.getValueType().equals(AnnotationProperty.VALUE_TYPE_RIF)) {
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
		Set<Long> xrefIds = new HashSet<Long>(); 
		for(Annotation a : annotations){
			if (a.getAPICategory()==AnnotationApiModel.BINARY_INTERACTION) {
				for (AnnotationProperty p: a.getProperties()) {
					if (p.getName().equals(AnnotationProperty.NAME_INTERACTANT)) {
						if (p.getValueType().equals(AnnotationProperty.VALUE_TYPE_RIF)) xrefIds.add(Long.parseLong(p.getValue()));
					}
				}
			}
		}
		return xrefIds;
	}


	public static Set<Long> getPublicationIdsForAnnotations(List<Annotation> annotations) {

		Set<Long> publicationIds = new HashSet<Long>(); 
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
			if (annot.getAPICategory()==AnnotationApiModel.SEQUENCE_CAUTION) {
				convertType2EvidenceToProperty(annot, AnnotationProperty.NAME_DIFFERING_SEQUENCE);
			} 
			else if (annot.getAPICategory()==AnnotationApiModel.COFACTOR) {
				convertType2EvidenceToProperty(annot, AnnotationProperty.NAME_COFACTOR);
			}
			else if (annot.getAPICategory()==AnnotationApiModel.DISEASE) {
				convertType2EvidenceToProperty(annot, AnnotationProperty.NAME_ALTERNATIVE_DISEASE_TERM);
			}
		}
	}
	

	private static void convertType2EvidenceToProperty(Annotation annot, String propertyName) {
		Iterator<AnnotationEvidence> evIt = annot.getEvidences().iterator();
		while (evIt.hasNext()) {
			AnnotationEvidence evi = evIt.next();
			if (evi.getResourceAssociationType().equals("relative")) {
				AnnotationProperty p = new AnnotationProperty();
				p.setAnnotationId(annot.getAnnotationId());
				p.setAccession(evi.getResourceAccession());
				p.setName(propertyName);
				p.setValue(""+evi.getResourceId());
				p.setValueType(AnnotationProperty.VALUE_TYPE_RIF);
				if (annot.getProperties()==null) annot.setProperties(new ArrayList<AnnotationProperty>());
				annot.getProperties().add(p);
				evIt.remove();
			}
		}
		
	}

}
