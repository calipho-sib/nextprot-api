package org.nextprot.api.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;


public class AnnotationUtils {
	
	
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

}
