package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.Feature;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.annotation.ValidEntry;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;


public interface AnnotationService {

	List<Annotation> findAnnotations(@ValidEntry String entryName);

	List<Feature> findPtmsByMaster(String uniqueName);

	List<Feature> findPtmsByIsoform(String uniqueName);

	List<Annotation> findAnnotationsExcludingBed(String entryName);

	/**
	 * Provide predicate on Annotation that tests if cvterm is the ancestor or one of his descendants
	 *
	 * @param ancestorAccession the ancestor cvterm accession
	 * @return a new predicate
	 */
	Predicate<Annotation> buildCvTermAncestorPredicate(String ancestorAccession);

	/**
	 * Provide predicate on Annotation that tests if either propertyName exists or propertyName/propertyValue exists
	 * depending on the definition of propertyValue
	 *
	 * @param propertyName the property name
	 * @param propertyValueOrAccession the property value or accession (can be null)
	 * @return a new predicate
	 */
	Predicate<Annotation> buildPropertyPredicate(String propertyName, @Nullable String propertyValueOrAccession);
}
