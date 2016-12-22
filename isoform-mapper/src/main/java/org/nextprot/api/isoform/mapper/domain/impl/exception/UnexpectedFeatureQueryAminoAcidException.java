package org.nextprot.api.isoform.mapper.domain.impl.exception;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.isoform.mapper.domain.SingleFeatureQuery;
import org.nextprot.api.isoform.mapper.domain.FeatureQueryException;

public class UnexpectedFeatureQueryAminoAcidException extends FeatureQueryException {

    private static final String EXPECTED_AAS = "expectedAminoAcids";
    private static final String FEATURE_AAS = "featureAminoAcids";

    public UnexpectedFeatureQueryAminoAcidException(SingleFeatureQuery query, int sequencePosition,
                                                    AminoAcidCode[] sequenceAminoAcidCodes, AminoAcidCode[] featureAminoAcidCodes) {
        super(query);

        getReason().addCause(EXPECTED_AAS, AminoAcidCode.formatAminoAcidCode(AminoAcidCode.CodeType.THREE_LETTER, sequenceAminoAcidCodes));
        getReason().addCause(FEATURE_AAS, AminoAcidCode.formatAminoAcidCode(AminoAcidCode.CodeType.THREE_LETTER, featureAminoAcidCodes));
        getReason().addCause(OutOfBoundSequencePositionException.SEQUENCE_POS, sequencePosition);

        getReason().setMessage(buildErrorMessage(sequenceAminoAcidCodes));
    }

    private String buildErrorMessage(AminoAcidCode[] sequenceAminoAcidCodes) {

        StringBuilder sb = new StringBuilder();

        sb
                .append("unexpected amino-acid")
                .append((sequenceAminoAcidCodes.length>1) ? "s" : "")
                .append(": found ").append(getFeatureAminoAcids())
                .append(" at position ").append(getIsoformSequencePosition())
                .append(" of ")
                .append(getQuery().getAccession())
                .append(" sequence instead of expected ").append(getExpectedAminoAcids());

        return sb.toString();
    }

    private String getFeatureAminoAcids() {
        return (String) getReason().getCause(FEATURE_AAS);
    }

    private String getExpectedAminoAcids() {
        return (String) getReason().getCause(EXPECTED_AAS);
    }

    private int getIsoformSequencePosition() {
        return (Integer) getReason().getCause(OutOfBoundSequencePositionException.SEQUENCE_POS);
    }
}
