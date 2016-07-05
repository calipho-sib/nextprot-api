package com.nextprot.api.isoform.mapper.domain.impl;

import com.nextprot.api.isoform.mapper.domain.MappedIsoformsFeatureError;

public class InvalidFeatureType extends MappedIsoformsFeatureError {

    static final String CATEGORY = "featureType";

    public InvalidFeatureType(Query query) {

        super(query);

        getError().setMessage("invalid feature type: cannot validate feature type " + query.getFeatureType());
        getError().addCause(CATEGORY, query.getFeatureType());
    }
}
