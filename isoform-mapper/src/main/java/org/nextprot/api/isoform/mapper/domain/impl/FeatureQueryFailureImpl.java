package org.nextprot.api.isoform.mapper.domain.impl;

import org.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import org.nextprot.api.isoform.mapper.domain.FeatureQueryFailure;

/**
 * Feature query failure class contain reason of the error
 */
public class FeatureQueryFailureImpl extends BaseFeatureQueryResult implements FeatureQueryFailure{

	private static final long serialVersionUID = 1L;
	private final transient FeatureQueryException.ErrorReason error;

    public FeatureQueryFailureImpl(FeatureQueryException error) {

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
