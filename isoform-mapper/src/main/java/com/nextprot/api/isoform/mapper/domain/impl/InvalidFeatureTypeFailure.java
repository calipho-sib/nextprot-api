package com.nextprot.api.isoform.mapper.domain.impl;

import com.nextprot.api.isoform.mapper.domain.MappedIsoformsFeatureFailure;

public class InvalidFeatureTypeFailure extends MappedIsoformsFeatureFailure {

    static final String CATEGORY = "featureType";

    public InvalidFeatureTypeFailure(FeatureQueryImpl query) {

        super(query);

        getError().setMessage("invalid feature type: cannot validate feature type " + query.getFeatureType());
        getError().addCause(CATEGORY, query.getFeatureType());
    }
}
