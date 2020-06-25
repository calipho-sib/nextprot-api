package org.nextprot.api.isoform.mapper.domain.impl.exception;

import org.nextprot.api.isoform.mapper.domain.query.SingleFeatureQuery;
import org.nextprot.api.isoform.mapper.domain.query.FeatureQueryException;

public class InvalidFeatureQueryTypeException extends FeatureQueryException {

    static final String CATEGORY = "featureType";

    public InvalidFeatureQueryTypeException(SingleFeatureQuery query) {

        super(query);

        getReason().setMessage("invalid feature type: cannot validate feature type " + query.getFeatureType());
        getReason().addCause(CATEGORY, query.getFeatureType());
    }
}
