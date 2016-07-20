package com.nextprot.api.isoform.mapper.domain.impl;

import org.nextprot.api.commons.bio.variation.SequenceVariationFormat;
import org.nextprot.api.commons.bio.variation.impl.format.bed.AminoAcidModificationBedFormat;
import org.nextprot.api.core.domain.Isoform;

import java.text.ParseException;

/**
 * A post translational modification on an isoform sequence
 */
public class SequenceModification extends SequenceFeatureBase {

    public SequenceModification(String feature) throws ParseException {
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

    @Override
    protected String parseIsoformName(String feature) throws ParseException {
        return null;
    }

    @Override
    protected String formatIsoformFeatureName(Isoform isoform) {
        return null;
    }
}
