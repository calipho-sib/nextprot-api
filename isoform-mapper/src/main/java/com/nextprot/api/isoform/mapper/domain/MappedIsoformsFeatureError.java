package com.nextprot.api.isoform.mapper.domain;

import org.nextprot.api.commons.bio.AminoAcidCode;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * A mapping result with specified error
 */
public class MappedIsoformsFeatureError extends MappedIsoformsFeatureResult {

    private FeatureErrorValue value;

    public MappedIsoformsFeatureError(Query query) {
        super(query);
    }

    public void setErrorValue(FeatureErrorValue value) {

        this.value = value;
    }

    public FeatureErrorValue getError() {
        return value;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    public static abstract class FeatureErrorValue implements Serializable {

        private static final long serialVersionUID = 1L;

        public abstract String getMessage();
    }

    public static class InvalidFeaturePosition extends FeatureErrorValue {

        private final String isoformAccession;
        private final int position;
        private final String message;

        public InvalidFeaturePosition(String isoformAccession, int position) {

            this.isoformAccession = isoformAccession;
            this.position = position;
            this.message = "invalid feature position "+position+" in sequence of isoform "+isoformAccession;
        }

        public int getPosition() {
            return position;
        }

        public String getIsoformAccession() {
            return isoformAccession;
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
            return position == that.position &&
                    Objects.equals(isoformAccession, that.isoformAccession);
        }

        @Override
        public int hashCode() {
            return Objects.hash(isoformAccession, position);
        }
    }

    public static class InvalidFeatureAminoAcid extends FeatureErrorValue {

        private final String isoformAccession;
        private final String featureAminoAcids;
        private final String sequenceAminoAcids;
        private final String feature;
        private final String message;
        private final int isoformSequencePosition;

        public InvalidFeatureAminoAcid(String isoformAccession, int isoformSequencePosition,
                                       AminoAcidCode[] sequenceAminoAcidCodes, AminoAcidCode[] featureAminoAcidCodes,
                                       String feature) {

            this.isoformAccession = isoformAccession;
            this.sequenceAminoAcids = AminoAcidCode.formatAminoAcidCode(AminoAcidCode.AACodeType.THREE_LETTER, sequenceAminoAcidCodes);
            this.featureAminoAcids = AminoAcidCode.formatAminoAcidCode(AminoAcidCode.AACodeType.THREE_LETTER, featureAminoAcidCodes);
            this.isoformSequencePosition = isoformSequencePosition;
            this.feature = feature;

            this.message = buildErrorMessage(sequenceAminoAcidCodes, featureAminoAcidCodes);
        }

        private String buildErrorMessage(AminoAcidCode[] sequenceAminoAcidCodes, AminoAcidCode[] featureAminoAcidCodes) {

            StringBuilder sb = new StringBuilder();

            sb
                    .append("invalid feature specification: ")
                    .append("found amino-acid").append(sequenceAminoAcidCodes.length > 1 ? "s " : " ").append(this.sequenceAminoAcids)
                    .append(" at position ").append(isoformSequencePosition)
                    .append(" of sequence isoform ")
                    .append(isoformAccession)
                    .append(" instead of ").append(this.featureAminoAcids)
                    .append(" as incorrectly specified in feature '")
                    .append(feature).append("'");

            return sb.toString();
        }


        public String getIsoformAccession() {
            return isoformAccession;
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
            return isoformSequencePosition == that.isoformSequencePosition &&
                    Objects.equals(isoformAccession, that.isoformAccession) &&
                    Objects.equals(featureAminoAcids, that.featureAminoAcids) &&
                    Objects.equals(sequenceAminoAcids, that.sequenceAminoAcids) &&
                    Objects.equals(message, that.message);
        }

        @Override
        public int hashCode() {
            return Objects.hash(isoformAccession, featureAminoAcids, sequenceAminoAcids, message, isoformSequencePosition);
        }
    }

    public static class InvalidFeatureFormat extends FeatureErrorValue {

        private final String feature;
        private final String message;

        public InvalidFeatureFormat(String feature) {

            this.feature = feature;
            this.message = "invalid feature format: "+feature;
        }

        public String getFeature() {
            return feature;
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
            return Objects.equals(feature, that.feature);
        }

        @Override
        public int hashCode() {
            return Objects.hash(feature);
        }
    }

    public static class IncompatibleGeneAndProteinName extends FeatureErrorValue {

        private final String geneName;
        private final String proteinName;
        private final List<String> expectedGeneNames;
        private final String message;

        public IncompatibleGeneAndProteinName(String geneName, String proteinName, List<String> expectedGeneNames) {

            this.geneName = geneName;
            this.proteinName = proteinName;
            this.expectedGeneNames = expectedGeneNames;
            this.message = "gene/protein incompatibility: protein "+proteinName+" is not compatible with gene "+geneName +" (expected genes: "+ expectedGeneNames+")";
        }

        public String getGeneName() {
            return geneName;
        }

        public String getProteinName() {
            return proteinName;
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
            return Objects.equals(geneName, that.geneName) &&
                    Objects.equals(proteinName, that.proteinName) &&
                    Objects.equals(expectedGeneNames, that.expectedGeneNames);
        }

        @Override
        public int hashCode() {
            return Objects.hash(geneName, proteinName, expectedGeneNames);
        }
    }
}
