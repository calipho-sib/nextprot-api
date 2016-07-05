package com.nextprot.api.isoform.mapper.domain.impl;

import com.nextprot.api.isoform.mapper.domain.MappedIsoformsFeatureFailure;

public class UnknownFeatureTypeFailure extends MappedIsoformsFeatureFailure {

    static final String CATEGORY = "featureType";

    public UnknownFeatureTypeFailure(Query query) {

        super(query);

        getError().setMessage("unknown feature type: cannot find feature type " + query.getFeatureType());
        getError().addCause(CATEGORY, query.getFeatureType());
    }
}
