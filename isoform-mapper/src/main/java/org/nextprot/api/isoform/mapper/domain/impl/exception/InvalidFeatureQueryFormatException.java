package org.nextprot.api.isoform.mapper.domain.impl.exception;

import java.text.ParseException;

import org.nextprot.api.isoform.mapper.domain.FeatureQuery;
import org.nextprot.api.isoform.mapper.domain.FeatureQueryException;

public class InvalidFeatureQueryFormatException extends FeatureQueryException {

    public static final String PARSE_ERROR_MESSAGE = "errorMessage";

    public InvalidFeatureQueryFormatException(FeatureQuery query, ParseException parseException) {

        super(query);

        getError().addCause(PARSE_ERROR_MESSAGE, parseException.getMessage());
        getError().setMessage("invalid feature format: " + query.getFeature());
    }
}
