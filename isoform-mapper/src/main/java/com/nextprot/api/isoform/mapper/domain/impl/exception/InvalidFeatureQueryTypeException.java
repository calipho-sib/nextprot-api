package com.nextprot.api.isoform.mapper.domain.impl.exception;

import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryException;

public class InvalidFeatureQueryTypeException extends FeatureQueryException {

    static final String CATEGORY = "featureType";

    public InvalidFeatureQueryTypeException(FeatureQuery query) {

        super(query);

        getError().setMessage("invalid feature type: cannot validate feature type " + query.getFeatureType());
        getError().addCause(CATEGORY, query.getFeatureType());
    }
}
