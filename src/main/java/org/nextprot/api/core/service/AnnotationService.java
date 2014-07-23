package org.nextprot.api.core.service;

import java.util.List;

import org.nextprot.api.core.domain.Feature;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.annotation.ValidEntry;


public interface AnnotationService {

	public List<Annotation> findAnnotations(@ValidEntry String entryName);
	
	List<Feature> findPtmsByMaster(String uniqueName);
	
	List<Feature> findPtmsByIsoform(String uniqueName);

}
