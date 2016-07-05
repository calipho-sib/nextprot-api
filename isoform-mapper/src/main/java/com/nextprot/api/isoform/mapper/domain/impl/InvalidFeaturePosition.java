package com.nextprot.api.isoform.mapper.domain.impl;

import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.MappedIsoformsFeatureError;

public class InvalidFeaturePosition extends MappedIsoformsFeatureError {

    static final String SEQUENCE_POS = "sequencePosition";

    public InvalidFeaturePosition(FeatureQuery query, int position) {

        super(query);

        getError().setMessage("invalid feature position: position " + position + " is out of bound in sequence of isoform " + query.getAccession());
        getError().addCause(SEQUENCE_POS, position);
    }
}
