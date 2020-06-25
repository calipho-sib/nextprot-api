package org.nextprot.api.isoform.mapper.domain.feature;


import org.nextprot.api.isoform.mapper.domain.query.RegionalFeatureQuery;
import org.nextprot.api.isoform.mapper.domain.query.result.FeatureQueryResult;

@FunctionalInterface
public interface RegionFeaturePropagator {

    /**
     * Compute the mapping of a single isoform feature on other isoforms.
     *
     * @param featureQuery the single feature query
     * @return a feature query result
     */
    FeatureQueryResult propagateFeature(RegionalFeatureQuery featureQuery);
}
