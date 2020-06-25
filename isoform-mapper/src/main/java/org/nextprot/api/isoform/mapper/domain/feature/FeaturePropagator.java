package org.nextprot.api.isoform.mapper.domain.feature;

import org.nextprot.api.isoform.mapper.domain.query.result.FeatureQueryResult;
import org.nextprot.api.isoform.mapper.domain.query.SingleFeatureQuery;

@FunctionalInterface
public interface FeaturePropagator {

    /**
     * Compute the mapping of a single isoform feature on other isoforms.
     *
     * @param featureQuery the single feature query
     * @return a feature query result
     */
    FeatureQueryResult propagateFeature(SingleFeatureQuery featureQuery);
}
