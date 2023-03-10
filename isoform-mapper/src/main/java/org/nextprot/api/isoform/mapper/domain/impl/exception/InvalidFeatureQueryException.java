package org.nextprot.api.isoform.mapper.domain.impl.exception;

import org.nextprot.api.isoform.mapper.domain.query.FeatureQueryException;
import org.nextprot.api.isoform.mapper.domain.query.SingleFeatureQuery;

public class InvalidFeatureQueryException extends FeatureQueryException {

    public InvalidFeatureQueryException(SingleFeatureQuery query, Exception e) {

        super(query);

        getReason().setMessage("invalid feature query: cannot validate feature " + query.getFeature());
        getReason().addCause("errorMessage", e.getMessage());
    }
}
