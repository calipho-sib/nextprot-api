package com.nextprot.api.isoform.mapper.service.impl;

import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.GeneVariationPair;
import com.nextprot.api.isoform.mapper.domain.impl.GeneVariantPair;
import com.nextprot.api.isoform.mapper.service.SequenceFeatureValidator;

import java.text.ParseException;

class SequenceVariantValidator extends SequenceFeatureValidator {

    public SequenceVariantValidator(FeatureQuery query) {
        super(query);
    }

    @Override
    protected GeneVariationPair newGeneVariationPair(String feature) throws ParseException {

        return new GeneVariantPair(feature);
    }
}
