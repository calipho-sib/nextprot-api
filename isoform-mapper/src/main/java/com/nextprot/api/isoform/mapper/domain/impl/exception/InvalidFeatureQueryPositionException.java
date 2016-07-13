package com.nextprot.api.isoform.mapper.domain.impl.exception;

import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryException;

public class InvalidFeatureQueryPositionException extends FeatureQueryException {

    static final String SEQUENCE_POS = "sequencePosition";

    public InvalidFeatureQueryPositionException(FeatureQuery query, int position) {

        super(query);

        getError().setMessage("invalid feature position: position " + position + " is out of bound in sequence of isoform " + query.getAccession());
        getError().addCause(SEQUENCE_POS, position);
    }
}
