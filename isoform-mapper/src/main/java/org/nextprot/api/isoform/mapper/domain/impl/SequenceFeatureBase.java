package org.nextprot.api.isoform.mapper.domain.impl;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariation;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariationBuildException;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariationFormatter;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariationParser;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.service.BeanService;
import org.nextprot.api.isoform.mapper.domain.SequenceFeature;
import org.nextprot.api.isoform.mapper.domain.impl.exception.PreIsoformParseException;

import java.text.ParseException;

/**
 * Parse and isoform name and protein sequence variation
 */
abstract class SequenceFeatureBase implements SequenceFeature {

    private final String feature;
    private final AnnotationCategory type;
    private final SequenceVariation variation;
    private final Isoform isoform;
    private final BeanService beanService;

    SequenceFeatureBase(String feature, AnnotationCategory type, SequenceVariationParser parser, BeanService beanService) throws ParseException, SequenceVariationBuildException {

        Preconditions.checkNotNull(feature);
        Preconditions.checkNotNull(type);
        Preconditions.checkNotNull(parser);
        Preconditions.checkNotNull(beanService);

        this.beanService = beanService;

        this.type = type;
        this.feature = feature.trim();

        String sequenceIdPart = parseSequenceIdPart();

        preIsoformParsing(sequenceIdPart);
        isoform = parseIsoform(sequenceIdPart);

        variation = parseVariation(parser, parseVariationPart());
    }

    protected void preIsoformParsing(String sequenceIdPart) throws PreIsoformParseException {}

    protected SequenceVariation parseVariation(SequenceVariationParser parser, String variationPart) throws ParseException, SequenceVariationBuildException {

        return parser.parse(variationPart);
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
                .append(getDelimitorBetweenIsoformAndVariation())
                .append(formatFeaturePart(getSequenceVariationFormatter(), isoVariation));

        return sb.toString();
    }

    @Override
    public SequenceVariation getProteinVariation() {
        return variation;
    }

    @Override
    public Isoform getIsoform() {

        return isoform;
    }

    BeanService getBeanService() {
        return beanService;
    }

    /**
     * @return the position in the feature string between the isoform part and the variation part
     * @throws ParseException
     */
    protected abstract int getDelimitingPositionBetweenIsoformAndVariation(String feature) throws ParseException;
    protected abstract String getDelimitorBetweenIsoformAndVariation();
    protected abstract SequenceVariationFormatter<String> getSequenceVariationFormatter();
    protected abstract String formatSequenceIdPart(Isoform isoform);
    protected abstract Isoform parseIsoform(String sequenceIdPart) throws ParseException;

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
    private String parseVariationPart() throws ParseException {

        return feature.substring(getDelimitingPositionBetweenIsoformAndVariation(feature)+1);
    }

    /**
     * @return the formatted feature part string
     */
    private String formatFeaturePart(SequenceVariationFormatter<String> formatter, SequenceVariation sequenceVariation) {

        return formatter.format(sequenceVariation, AminoAcidCode.CodeType.THREE_LETTER);
    }
}
