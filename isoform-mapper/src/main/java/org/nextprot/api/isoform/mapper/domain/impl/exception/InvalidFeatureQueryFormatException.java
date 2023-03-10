package org.nextprot.api.isoform.mapper.domain.impl.exception;

import org.nextprot.api.isoform.mapper.domain.query.FeatureQueryException;
import org.nextprot.api.isoform.mapper.domain.query.SingleFeatureQuery;

public class InvalidFeatureQueryFormatException extends FeatureQueryException {

    public static final String ERROR_MESSAGE = "errorMessage";

    public InvalidFeatureQueryFormatException(SingleFeatureQuery query, Exception exception) {

        super(query);

        getReason().addCause(ERROR_MESSAGE, exception.getMessage());
        getReason().setMessage("invalid feature format: " + query.getFeature());
    }
}
