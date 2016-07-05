package com.nextprot.api.isoform.mapper.domain.impl;

import com.google.common.base.Preconditions;
import com.nextprot.api.isoform.mapper.domain.FeatureQuery;

import java.io.Serializable;

public class FeatureQueryImpl implements FeatureQuery, Serializable {

    private final String accession;
    private final String feature;
    private final String featureType;
    private final boolean propagableFeature;

    public FeatureQueryImpl(String accession, String feature, String featureType, boolean propagableFeature) {

        Preconditions.checkNotNull(accession);
        Preconditions.checkArgument(accession.startsWith("NX_"), "should be a nextprot accession number");
        Preconditions.checkNotNull(feature);
        Preconditions.checkArgument(!feature.isEmpty(), "feature should be defined");
        Preconditions.checkNotNull(featureType);

        this.accession = accession;
        this.feature = feature;
        this.featureType = featureType;
        this.propagableFeature = propagableFeature;
    }

    @Override
    public String getAccession() {
        return accession;
    }

    @Override
    public String getFeature() {
        return feature;
    }

    @Override
    public String getFeatureType() {
        return featureType;
    }

    @Override
    public boolean isFeaturePropagable() {
        return propagableFeature;
    }
}
