package com.nextprot.api.isoform.mapper.domain.impl;

import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.MappedIsoformsFeatureError;

import java.text.ParseException;

public class InvalidFeatureFormat extends MappedIsoformsFeatureError {

    public static final String PARSE_ERROR_MESSAGE = "parseErrorMessage";
    public static final String PARSE_ERROR_OFFSET = "parseErrorOffset";

    public InvalidFeatureFormat(FeatureQuery query, ParseException parseException) {

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
