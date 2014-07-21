package org.nextprot.api.service;

import java.util.List;

import org.nextprot.api.annotation.ValidEntry;
import org.nextprot.api.domain.Feature;
import org.nextprot.api.domain.annotation.Annotation;


public interface AnnotationService {

	public List<Annotation> findAnnotations(@ValidEntry String entryName);
	
	List<Feature> findPtmsByMaster(String uniqueName);
	
	List<Feature> findPtmsByIsoform(String uniqueName);

}
