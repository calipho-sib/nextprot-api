package org.nextprot.api.isoform.mapper.domain.impl.exception;

import org.nextprot.api.isoform.mapper.domain.FeatureQuery;
import org.nextprot.api.isoform.mapper.domain.FeatureQueryException;

public class UndefinedFeatureQueryException extends FeatureQueryException {

    public UndefinedFeatureQueryException(FeatureQuery query) {

        super(query);

        getReason().setMessage("undefined feature");
    }
}
