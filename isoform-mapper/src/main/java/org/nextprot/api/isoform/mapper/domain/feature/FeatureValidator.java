package org.nextprot.api.isoform.mapper.domain.feature;

import org.nextprot.api.isoform.mapper.domain.query.result.FeatureQueryResult;
import org.nextprot.api.isoform.mapper.domain.query.SingleFeatureQuery;

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
