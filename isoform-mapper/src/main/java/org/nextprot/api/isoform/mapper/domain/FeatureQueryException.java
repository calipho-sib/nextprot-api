package org.nextprot.api.isoform.mapper.domain;

import org.nextprot.api.commons.utils.ExceptionWithReason;

public abstract class FeatureQueryException extends ExceptionWithReason {

    private static final long serialVersionUID = 2L;

    private final FeatureQuery query;
    private final Reason error;

    public FeatureQueryException(FeatureQuery query) {

        this.query = query;
        error = new Reason();
    }

    public FeatureQuery getQuery() {

        return query;
    }

    @Override
    public Reason getReason() {

        return error;
    }
}
