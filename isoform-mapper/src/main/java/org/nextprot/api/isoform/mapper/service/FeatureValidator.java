package org.nextprot.api.isoform.mapper.service;

import org.nextprot.api.isoform.mapper.domain.FeatureQueryResult;
import org.nextprot.api.isoform.mapper.domain.SingleFeatureQuery;

/**
 * Validate features
 */
@FunctionalInterface
public interface FeatureValidator {

    /**
     * Check that the specified feature is valid on the given entry
     *
     * @param featureQuery the single feature query
     * @return a feature query result
     */
    FeatureQueryResult validateFeature(SingleFeatureQuery featureQuery);
}
