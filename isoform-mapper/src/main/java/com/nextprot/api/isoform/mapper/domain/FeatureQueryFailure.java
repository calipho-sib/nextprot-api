package com.nextprot.api.isoform.mapper.domain;

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
