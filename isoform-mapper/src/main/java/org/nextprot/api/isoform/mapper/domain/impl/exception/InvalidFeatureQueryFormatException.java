package org.nextprot.api.isoform.mapper.domain.impl.exception;

import org.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import org.nextprot.api.isoform.mapper.domain.SingleFeatureQuery;

import java.text.ParseException;

public class InvalidFeatureQueryFormatException extends FeatureQueryException {

    public static final String PARSE_ERROR_MESSAGE = "errorMessage";

    public InvalidFeatureQueryFormatException(SingleFeatureQuery query, ParseException parseException) {

        super(query);

        getError().addCause(PARSE_ERROR_MESSAGE, parseException.getMessage());
        getError().setMessage("invalid feature format: " + query.getFeature());
    }
}
