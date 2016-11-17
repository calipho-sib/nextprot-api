package org.nextprot.api.isoform.mapper.domain.impl.exception;

import org.nextprot.api.isoform.mapper.domain.SingleFeatureQuery;
import org.nextprot.api.isoform.mapper.domain.FeatureQueryException;

public class InvalidFeatureQueryTypeException extends FeatureQueryException {

    static final String CATEGORY = "featureType";

    public InvalidFeatureQueryTypeException(SingleFeatureQuery query) {

        super(query);

        getError().setMessage("invalid feature type: cannot validate feature type " + query.getFeatureType());
        getError().addCause(CATEGORY, query.getFeatureType());
    }
}
