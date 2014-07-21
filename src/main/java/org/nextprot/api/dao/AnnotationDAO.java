package org.nextprot.api.dao;

import java.util.List;

import org.nextprot.api.domain.annotation.Annotation;
import org.nextprot.api.domain.annotation.AnnotationEvidence;
import org.nextprot.api.domain.annotation.AnnotationEvidenceProperty;
import org.nextprot.api.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.domain.annotation.AnnotationProperty;
import org.springframework.beans.factory.annotation.Value;

public interface AnnotationDAO {

	/**
	 * Returns a list of Annotations with uninitialized collections
	 * @param The name of the entry
	 * @return 
	 */
	List<Annotation> findAnnotationsByEntryName(@Value("entryName") String entryName);

	List<AnnotationIsoformSpecificity> findAnnotationIsoformsByAnnotationIds(List<Long> annotationIds);

	List<AnnotationEvidence> findAnnotationEvidencesByAnnotationIds(List<Long> annotationIds);

	List<AnnotationProperty> findAnnotationPropertiesByAnnotationIds(List<Long> annotationIds);
	
	List<AnnotationEvidenceProperty> findAnnotationEvidencePropertiesByEvidenceIds(List<Long> evidenceIds);
}
