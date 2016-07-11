package com.nextprot.api.isoform.mapper.domain.impl;

import org.nextprot.api.commons.bio.variation.SequenceVariationFormat;
import org.nextprot.api.commons.bio.variation.impl.format.bed.AminoAcidModificationBedFormat;

import java.text.ParseException;

public class GenePtmPair extends GeneFeaturePair {

    public GenePtmPair(String feature) throws ParseException {
        super(feature);
    }

    @Override
    protected String parseVariation(String feature) {

        int dashPosition = feature.indexOf("-");
        return feature.substring(dashPosition + 1);
    }

    @Override
    public SequenceVariationFormat newParser() {

        return new AminoAcidModificationBedFormat();
    }
}
