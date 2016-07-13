package com.nextprot.api.isoform.mapper.domain.impl.exception;

import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryException;

import java.text.ParseException;

public class InvalidFeatureQueryFormatException extends FeatureQueryException {

    public static final String PARSE_ERROR_MESSAGE = "parseErrorMessage";
    public static final String PARSE_ERROR_OFFSET = "parseErrorOffset";

    public InvalidFeatureQueryFormatException(FeatureQuery query, ParseException parseException) {

        super(query);

        getError().addCause(PARSE_ERROR_MESSAGE, parseException.getMessage());
        getError().addCause(PARSE_ERROR_OFFSET, parseException.getErrorOffset());
        getError().setMessage("invalid feature format: " + query.getFeature());
    }

    String getParseErrorMessage() {
        return (String) getError().getCause(PARSE_ERROR_MESSAGE);
    }

    int getParseErrorOffset() {
        return (Integer) getError().getCause(PARSE_ERROR_OFFSET);
    }
}
