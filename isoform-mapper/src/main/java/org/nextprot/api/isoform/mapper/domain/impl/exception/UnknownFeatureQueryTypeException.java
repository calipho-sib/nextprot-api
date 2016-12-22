package org.nextprot.api.isoform.mapper.domain.impl.exception;

import org.nextprot.api.isoform.mapper.domain.FeatureQuery;
import org.nextprot.api.isoform.mapper.domain.FeatureQueryException;

public class UnknownFeatureQueryTypeException extends FeatureQueryException {

    static final String CATEGORY = "featureType";

    public UnknownFeatureQueryTypeException(FeatureQuery query) {

        super(query);

        getReason().setMessage("unknown feature type: cannot find feature type " + query.getFeatureType());
        getReason().addCause(CATEGORY, query.getFeatureType());
    }
}
