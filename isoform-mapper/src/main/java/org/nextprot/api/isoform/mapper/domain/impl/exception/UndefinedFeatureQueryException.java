package org.nextprot.api.isoform.mapper.domain.impl.exception;

import org.nextprot.api.isoform.mapper.domain.query.FeatureQuery;
import org.nextprot.api.isoform.mapper.domain.query.FeatureQueryException;

public class UndefinedFeatureQueryException extends FeatureQueryException {

    public UndefinedFeatureQueryException(FeatureQuery query) {

        super(query);

        getReason().setMessage("undefined feature");
    }
}
