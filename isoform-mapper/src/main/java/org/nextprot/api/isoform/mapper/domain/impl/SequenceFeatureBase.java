package org.nextprot.api.isoform.mapper.domain.impl;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariation;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariationFormat;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.BeanService;
import org.nextprot.api.isoform.mapper.domain.SequenceFeature;

import java.text.ParseException;

/**
 * Parse and isoform name and protein sequence variation
 */
public abstract class SequenceFeatureBase implements SequenceFeature {

    private final String feature;
    private final AnnotationCategory type;
    protected final String sequenceIdPart;
    private final String variationPart;
    private final SequenceVariation variation;
    private final SequenceVariationFormat parser;
    protected final BeanService beanService;

    SequenceFeatureBase(String feature, AnnotationCategory type, BeanService beanService) throws ParseException {

        Preconditions.checkNotNull(feature);
        Preconditions.checkNotNull(type);

        this.type = type;
        this.feature = feature.trim();

        sequenceIdPart = parseSequenceIdPart();
        variationPart = parseVariationPart();

        parser = newParser();
        variation = parser.parse(variationPart);

        this.beanService = beanService;
    }

    @Override
    public AnnotationCategory getType() {
        return type;
    }

    // TODO: does this method reside in the correct object ?
    @Override
    public String formatIsoSpecificFeature(Isoform isoform, int firstPos, int lastPos) {

        // create a new variation specific to the isoform
        SequenceVariationMutable isoVariation = new SequenceVariationMutable();

        VaryingSequenceMutable changingSequence = new VaryingSequenceMutable();
        changingSequence.setFirst(variation.getVaryingSequence().getFirstAminoAcid());
        changingSequence.setLast(variation.getVaryingSequence().getLastAminoAcid());
        changingSequence.setFirstPos(firstPos);
        changingSequence.setLastPos(lastPos);

        isoVariation.setVaryingSequence(changingSequence);
        isoVariation.setSequenceChange(variation.getSequenceChange());

        StringBuilder sb = new StringBuilder()
                .append(formatSequenceIdPart(isoform))
                .append("-")
                .append(formatFeaturePart(isoVariation));

        return sb.toString();
    }

    @Override
    public SequenceVariation getProteinVariation() {
        return variation;
    }

    /**
     * @return the position in the feature string between the isoform part and the variation part
     * @throws ParseException
     */
    protected abstract int getDelimitingPositionBetweenIsoformAndVariation(String feature) throws ParseException;
    protected abstract SequenceVariationFormat newParser();
    protected abstract String formatSequenceIdPart(Isoform isoform);

    /**
     * @return the sequence id part from the feature string
     * @throws ParseException if invalid format
     */
    private String parseSequenceIdPart() throws ParseException {

        return feature.substring(0, getDelimitingPositionBetweenIsoformAndVariation(feature));
    }

    /**
     * @return the variation part from the feature string
     * @throws ParseException if invalid format
     */
    String parseVariationPart() throws ParseException {

        return feature.substring(getDelimitingPositionBetweenIsoformAndVariation(feature)+1);
    }

    /**
     * @return the formatted feature part string
     */
    private String formatFeaturePart(SequenceVariation sequenceVariation) {

        return parser.format(sequenceVariation, AminoAcidCode.CodeType.THREE_LETTER);
    }
}
