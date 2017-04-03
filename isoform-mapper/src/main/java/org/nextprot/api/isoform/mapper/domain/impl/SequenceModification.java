package org.nextprot.api.isoform.mapper.domain.impl;

import org.nextprot.api.commons.bio.variation.SequenceVariationFormat;
import org.nextprot.api.commons.bio.variation.impl.format.SequenceModificationBedFormat;
import org.nextprot.api.core.domain.Isoform;

import java.text.ParseException;

/**
 * A post translational modification on an isoform sequence
 */
public class SequenceModification extends SequenceFeatureBase {

    public SequenceModification(String feature) throws ParseException {
        super(feature);
    }

    // TODO: Implement this method properly
    @Override
    protected int getPivotPoint(String feature) throws ParseException {

        return feature.indexOf("-");
    }

    @Override
    public SequenceVariationFormat newParser() {

        return new SequenceModificationBedFormat();
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
