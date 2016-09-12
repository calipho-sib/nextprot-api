package org.nextprot.api.isoform.mapper.domain.impl;

import org.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import org.nextprot.api.isoform.mapper.domain.FeatureQueryResult;

/**
 * Feature query failure class contain reason of the error
 */
public class FeatureQueryFailure extends FeatureQueryResult {

    private final FeatureQueryException.ErrorReason error;

    public FeatureQueryFailure(FeatureQueryException error) {

        super(error.getQuery());
        this.error = error.getError();
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    public FeatureQueryException.ErrorReason getError() {

        return error;
    }
}
