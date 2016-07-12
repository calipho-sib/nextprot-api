package com.nextprot.api.isoform.mapper.service;

import org.nextprot.api.commons.constants.AnnotationCategory;

import java.util.Optional;

/**
 * Create FeatureValidators
 */
public interface FeatureValidatorFactoryService {

    /**
     * Creates a new instance of SequenceFeatureValidator given an annotation category
     *
     * @param annotationCategory annotation category
     * @return Optional.empty if not found
     */
    Optional<SequenceFeatureValidator> createsFeatureValidator(AnnotationCategory annotationCategory);
}
