package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.Feature;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.annotation.ValidEntry;

import java.util.List;


public interface AnnotationService {

	List<Annotation> findAnnotations(@ValidEntry String entryName);
	
	List<Feature> findPtmsByMaster(String uniqueName);
	
	List<Feature> findPtmsByIsoform(String uniqueName);

	List<Annotation> findAnnotationsExcludingBed(String entryName);

	List<Annotation> filterByCvTermAncestor(List<Annotation> annotations, String ancestorTermAccession);
}
