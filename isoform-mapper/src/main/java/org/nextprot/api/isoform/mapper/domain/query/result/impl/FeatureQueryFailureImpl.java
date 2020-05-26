package org.nextprot.api.isoform.mapper.domain.query.result.impl;

import org.nextprot.api.commons.utils.ExceptionWithReason;
import org.nextprot.api.isoform.mapper.domain.query.FeatureQueryException;
import org.nextprot.api.isoform.mapper.domain.query.result.FeatureQueryFailure;
import org.nextprot.api.isoform.mapper.domain.query.result.impl.BaseFeatureQueryResult;

/**
 * Feature query failure class contain reason of the error
 */
public class FeatureQueryFailureImpl extends BaseFeatureQueryResult implements FeatureQueryFailure{

	private static final long serialVersionUID = 1L;
	private final transient ExceptionWithReason.Reason error;

    public FeatureQueryFailureImpl(FeatureQueryException error) {

        super(error.getQuery());
        this.error = error.getReason();
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    public ExceptionWithReason.Reason getError() {

        return error;
    }
}
