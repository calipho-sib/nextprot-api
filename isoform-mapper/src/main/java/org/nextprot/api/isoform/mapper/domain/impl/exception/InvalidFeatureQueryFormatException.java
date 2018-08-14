package org.nextprot.api.isoform.mapper.domain.impl.exception;

import org.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import org.nextprot.api.isoform.mapper.domain.SingleFeatureQuery;

public class InvalidFeatureQueryFormatException extends FeatureQueryException {

    public static final String PARSE_ERROR_MESSAGE = "errorMessage";

    public InvalidFeatureQueryFormatException(SingleFeatureQuery query, Exception exception) {

        super(query);

        getReason().addCause(PARSE_ERROR_MESSAGE, exception.getMessage());
        getReason().setMessage("invalid feature format: " + query.getFeature());
    }
}
