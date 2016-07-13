package com.nextprot.api.isoform.mapper.domain.impl.exception;

import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryException;

public class UndefinedFeatureQueryException extends FeatureQueryException {

    public UndefinedFeatureQueryException(FeatureQuery query) {

        super(query);

        getError().setMessage("undefined feature");
    }
}
