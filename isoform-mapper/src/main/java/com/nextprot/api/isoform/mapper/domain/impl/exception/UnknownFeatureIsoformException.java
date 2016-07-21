package com.nextprot.api.isoform.mapper.domain.impl.exception;

import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryException;

public class UnknownFeatureIsoformException extends FeatureQueryException {

    private static final String INVALID_FEATURE = "invalidFeature";

    public UnknownFeatureIsoformException(FeatureQuery query) {

        super(query);

        getError().addCause(INVALID_FEATURE, query.getFeature());
        getError().setMessage("unknown isoform: isoform defined in feature " + query.getFeature()
                + " not found in entry "+ query.getAccession());
    }
}
