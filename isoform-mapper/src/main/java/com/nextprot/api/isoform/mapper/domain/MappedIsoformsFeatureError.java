package com.nextprot.api.isoform.mapper.domain;

import org.nextprot.api.commons.bio.AminoAcidCode;

import java.util.List;
import java.util.Objects;

/**
 * A mapping result with specified error
 */
public abstract class MappedIsoformsFeatureError extends MappedIsoformsFeatureResult {

    public MappedIsoformsFeatureError(Query query) {
        super(query);
    }

    public MappedIsoformsFeatureError getError() {
        return this;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    public abstract String getMessage();

    public static class InvalidFeaturePosition extends MappedIsoformsFeatureError {

        private final int position;
        private final String message;

        public InvalidFeaturePosition(Query query, int position) {

            super(query);

            this.position = position;
            this.message = "invalid feature position: position "+position+" is out of bound in sequence of isoform "+query.getAccession();
        }

        public int getPosition() {
            return position;
        }

        @Override
        public String getMessage() {
            return message;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof InvalidFeaturePosition)) return false;
            InvalidFeaturePosition that = (InvalidFeaturePosition) o;
            return Objects.equals(message, that.message);
        }

        @Override
        public int hashCode() {
            return Objects.hash(message);
        }
    }

    public static class InvalidFeatureAminoAcid extends MappedIsoformsFeatureError {

        private final String featureAminoAcids;
        private final String sequenceAminoAcids;
        private final String message;
        private final int isoformSequencePosition;

        public InvalidFeatureAminoAcid(Query query, int isoformSequencePosition,
                                       AminoAcidCode[] sequenceAminoAcidCodes, AminoAcidCode[] featureAminoAcidCodes) {

            super(query);

            this.sequenceAminoAcids = AminoAcidCode.formatAminoAcidCode(AminoAcidCode.AACodeType.THREE_LETTER, sequenceAminoAcidCodes);
            this.featureAminoAcids = AminoAcidCode.formatAminoAcidCode(AminoAcidCode.AACodeType.THREE_LETTER, featureAminoAcidCodes);
            this.isoformSequencePosition = isoformSequencePosition;

            this.message = buildErrorMessage(sequenceAminoAcidCodes, featureAminoAcidCodes);
        }

        private String buildErrorMessage(AminoAcidCode[] sequenceAminoAcidCodes, AminoAcidCode[] featureAminoAcidCodes) {

            StringBuilder sb = new StringBuilder();

            sb
                    .append("invalid feature specification: ")
                    .append("found amino-acid").append(sequenceAminoAcidCodes.length > 1 ? "s " : " ").append(this.sequenceAminoAcids)
                    .append(" at position ").append(isoformSequencePosition)
                    .append(" of sequence isoform ")
                    .append(getQuery().getAccession())
                    .append(" instead of ").append(this.featureAminoAcids)
                    .append(" as incorrectly specified in feature '")
                    .append(getQuery().getFeature()).append("'");

            return sb.toString();
        }

        public String getFeatureAminoAcids() {
            return featureAminoAcids;
        }

        public String getSequenceAminoAcids() {
            return sequenceAminoAcids;
        }

        public int getIsoformSequencePosition() {
            return isoformSequencePosition;
        }

        @Override
        public String getMessage() {
            return message;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof InvalidFeatureAminoAcid)) return false;
            InvalidFeatureAminoAcid that = (InvalidFeatureAminoAcid) o;
            return Objects.equals(message, that.message);
        }

        @Override
        public int hashCode() {
            return Objects.hash(message);
        }
    }

    public static class InvalidFeatureFormat extends MappedIsoformsFeatureError {

        private final String message;

        public InvalidFeatureFormat(Query query) {

            super(query);

            this.message = "invalid feature format: "+query.getFeature();
        }

        @Override
        public String getMessage() {
            return message;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof InvalidFeatureFormat)) return false;
            InvalidFeatureFormat that = (InvalidFeatureFormat) o;
            return Objects.equals(message, that.message);
        }

        @Override
        public int hashCode() {
            return Objects.hash(message);
        }
    }

    public static class IncompatibleGeneAndProteinName extends MappedIsoformsFeatureError {

        private final String geneName;
        private final List<String> expectedGeneNames;
        private final String message;

        public IncompatibleGeneAndProteinName(Query query, String geneName, List<String> expectedGeneNames) {

            super(query);

            this.geneName = geneName;
            this.expectedGeneNames = expectedGeneNames;
            this.message = "gene/protein incompatibility: protein "+query.getAccession()+" is not compatible with gene "+geneName +" (expected genes: "+ expectedGeneNames+")";
        }

        public String getGeneName() {
            return geneName;
        }

        public List<String> getExpectedGeneNames() {
            return expectedGeneNames;
        }

        @Override
        public String getMessage() {
            return message;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof IncompatibleGeneAndProteinName)) return false;
            IncompatibleGeneAndProteinName that = (IncompatibleGeneAndProteinName) o;
            return Objects.equals(message, that.message);
        }

        @Override
        public int hashCode() {
            return Objects.hash(message);
        }
    }
}
