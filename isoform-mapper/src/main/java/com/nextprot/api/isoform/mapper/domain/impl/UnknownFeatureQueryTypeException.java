package com.nextprot.api.isoform.mapper.domain.impl;

import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryException;

public class UnknownFeatureQueryTypeException extends FeatureQueryException {

    static final String CATEGORY = "featureType";

    public UnknownFeatureQueryTypeException(FeatureQuery query) {

        super(query);

        getError().setMessage("unknown feature type: cannot find feature type " + query.getFeatureType());
        getError().addCause(CATEGORY, query.getFeatureType());
    }
}
