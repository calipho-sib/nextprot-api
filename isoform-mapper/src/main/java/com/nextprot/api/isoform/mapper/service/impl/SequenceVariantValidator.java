package com.nextprot.api.isoform.mapper.service.impl;

import com.nextprot.api.isoform.mapper.domain.GeneVariationPair;
import com.nextprot.api.isoform.mapper.domain.impl.GeneVariantPair;

import java.text.ParseException;

public class SequenceVariantValidator extends SequenceVariationValidator {

    @Override
    protected GeneVariationPair newGeneVariationPair(String feature) throws ParseException {

        return new GeneVariantPair(feature);
    }
}
