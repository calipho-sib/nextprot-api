package com.nextprot.api.isoform.mapper.domain.impl;

import com.nextprot.api.isoform.mapper.domain.FeatureQuery;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryException;
import org.nextprot.api.commons.bio.AminoAcidCode;

public class InvalidFeatureQueryAminoAcidException extends FeatureQueryException {

    private static final String SEQUENCE_AAS = "sequenceAminoAcids";
    private static final String FEATURE_AAS = "featureAminoAcids";

    public InvalidFeatureQueryAminoAcidException(FeatureQuery query, int isoformSequencePosition,
                                                 AminoAcidCode[] sequenceAminoAcidCodes, AminoAcidCode[] featureAminoAcidCodes) {
        super(query);

        getError().addCause(SEQUENCE_AAS, AminoAcidCode.formatAminoAcidCode(AminoAcidCode.AACodeType.THREE_LETTER, sequenceAminoAcidCodes));
        getError().addCause(FEATURE_AAS, AminoAcidCode.formatAminoAcidCode(AminoAcidCode.AACodeType.THREE_LETTER, featureAminoAcidCodes));
        getError().addCause(InvalidFeatureQueryPositionException.SEQUENCE_POS, isoformSequencePosition);

        getError().setMessage(buildErrorMessage(sequenceAminoAcidCodes));
    }

    private String buildErrorMessage(AminoAcidCode[] sequenceAminoAcidCodes) {

        StringBuilder sb = new StringBuilder();

        sb
                .append("invalid feature specification: ")
                .append("found amino-acid").append(sequenceAminoAcidCodes.length > 1 ? "s " : " ").append(getSequenceAminoAcids())
                .append(" at position ").append(getIsoformSequencePosition())
                .append(" of sequence isoform ")
                .append(getQuery().getAccession())
                .append(" instead of ").append(getFeatureAminoAcids())
                .append(" as incorrectly specified in feature '")
                .append(getQuery().getFeature()).append("'");

        return sb.toString();
    }

    private String getFeatureAminoAcids() {
        return (String) getError().getCause(FEATURE_AAS);
    }

    private String getSequenceAminoAcids() {
        return (String) getError().getCause(SEQUENCE_AAS);
    }

    private int getIsoformSequencePosition() {
        return (Integer) getError().getCause(InvalidFeatureQueryPositionException.SEQUENCE_POS);
    }
}
