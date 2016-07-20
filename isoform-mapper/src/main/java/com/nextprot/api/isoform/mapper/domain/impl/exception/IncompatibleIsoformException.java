package com.nextprot.api.isoform.mapper.domain.impl.exception;

import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryException;

public class IncompatibleIsoformException extends FeatureQueryException {

    private static final String ISOFORM_NAME = "isoformName";
    private static final String EXPECTED_ISO_NAME = "expectedIsoformName";

    public IncompatibleIsoformException(FeatureQuery query, String expectedIsoformName) {

        super(query);

        getError().addCause(ISOFORM_NAME, query.getAccession());
        getError().addCause(EXPECTED_ISO_NAME, expectedIsoformName);
        getError().setMessage("query/feature isoform incompatibility: query isoform " + query.getAccession() + " is not compatible with isoform " + expectedIsoformName + " defined in feature");
    }
}
