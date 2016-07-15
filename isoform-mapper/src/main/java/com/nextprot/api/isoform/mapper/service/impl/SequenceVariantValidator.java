package com.nextprot.api.isoform.mapper.service.impl;

import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.SequenceFeature;
import com.nextprot.api.isoform.mapper.domain.impl.SequenceVariant;
import com.nextprot.api.isoform.mapper.service.SequenceFeatureValidator;

import java.text.ParseException;

class SequenceVariantValidator extends SequenceFeatureValidator {

    SequenceVariantValidator(FeatureQuery query) {
        super(query);
    }

    @Override
    protected SequenceFeature newSequenceFeature(String feature) throws ParseException {

        return new SequenceVariant(feature);
    }
}
