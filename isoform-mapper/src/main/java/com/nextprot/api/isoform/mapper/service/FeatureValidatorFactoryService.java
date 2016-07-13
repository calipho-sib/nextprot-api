package com.nextprot.api.isoform.mapper.service;

import com.nextprot.api.isoform.mapper.domain.FeatureQuery;

import java.util.Optional;

/**
 * Create FeatureValidators
 */
public interface FeatureValidatorFactoryService {

    /**
     * Creates a new instance of SequenceFeatureValidator given an annotation category
     *
     * @param query feature query
     * @return Optional.empty if not found
     */
    Optional<SequenceFeatureValidator> createsFeatureValidator(FeatureQuery query);
}
