package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.Feature;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.service.annotation.ValidEntry;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;


public interface AnnotationService {

	List<Annotation> findAnnotations(@ValidEntry String entryName);

	Map<String, List<Annotation>> findAnnotationsByCategory(@ValidEntry String entryName, List<String> annotationCategory);

	List<Feature> findPtmsByMaster(String uniqueName);

	List<Feature> findPtmsByIsoform(String uniqueName);

	List<Annotation> findAnnotationsExcludingBed(String entryName);

	/**
	 * Provides a predicate object that test if an Annotation cvterm is a descendant of the given cvterm ancestor
	 *
	 * @param ancestorAccession the ancestor cvterm accession
	 * @return a new predicate
	 */
	Predicate<Annotation> createDescendantTermPredicate(String ancestorAccession);

	/**
	 * Provides a predicate object that test if an AnnotationEvidence evidence code is a descendant of the given evidence ancestor
	 *
	 * @param ancestorEvidenceCode the ancestor evidence code accession
	 * @return a new predicate
	 */
	Predicate<AnnotationEvidence> createDescendantEvidenceTermPredicate(String ancestorEvidenceCode);

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
