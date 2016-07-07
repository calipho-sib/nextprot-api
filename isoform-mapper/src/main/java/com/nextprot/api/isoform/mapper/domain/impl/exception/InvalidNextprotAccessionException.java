package com.nextprot.api.isoform.mapper.domain.impl.exception;

import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryException;

public class InvalidNextprotAccessionException extends FeatureQueryException {

    public InvalidNextprotAccessionException(FeatureQuery query) {

        super(query);

        getError().setMessage("invalid nextprot accession number: " + query.getAccession());
    }
}
