package com.nextprot.api.isoform.mapper.domain.impl;

import org.nextprot.api.commons.bio.variation.SequenceVariationFormat;
import org.nextprot.api.commons.bio.variation.impl.format.hgvs.SequenceVariationHGVSFormat;

import java.text.ParseException;

public class GeneVariantPair extends GeneFeaturePair {

    public GeneVariantPair(String feature) throws ParseException {
        super(feature);
    }

    @Override
    public SequenceVariationFormat newParser() {
        return new SequenceVariationHGVSFormat();
    }
}
