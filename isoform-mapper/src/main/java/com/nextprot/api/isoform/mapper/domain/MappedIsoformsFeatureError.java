package com.nextprot.api.isoform.mapper.domain;

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

        private final String message;

        public FeatureErrorValue(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class InvalidFeaturePosition extends FeatureErrorValue {

        private final String isoformAccession;
        private final int position;

        public InvalidFeaturePosition(String isoformAccession, int position) {
            super("invalid feature position "+position+" in sequence of isoform "+isoformAccession);

            this.isoformAccession = isoformAccession;
            this.position = position;
        }

        public int getPosition() {
            return position;
        }

        public String getIsoformAccession() {
            return isoformAccession;
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

        public InvalidFeatureAminoAcid(String isoformAccession, String sequenceAminoAcids, String featureAminoAcids) {
            super("invalid feature amino-acid(s) "+featureAminoAcids+" instead of expected "+sequenceAminoAcids+" in sequence of isoform "+isoformAccession);

            this.isoformAccession = isoformAccession;
            this.featureAminoAcids = featureAminoAcids;
            this.sequenceAminoAcids = sequenceAminoAcids;
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof InvalidFeatureAminoAcid)) return false;
            InvalidFeatureAminoAcid that = (InvalidFeatureAminoAcid) o;
            return Objects.equals(isoformAccession, that.isoformAccession) &&
                    Objects.equals(featureAminoAcids, that.featureAminoAcids) &&
                    Objects.equals(sequenceAminoAcids, that.sequenceAminoAcids);
        }

        @Override
        public int hashCode() {
            return Objects.hash(isoformAccession, featureAminoAcids, sequenceAminoAcids);
        }
    }

    public static class InvalidFeatureFormat extends FeatureErrorValue {

        private final String variant;

        public InvalidFeatureFormat(String variant) {
            super("invalid feature format "+variant);

            this.variant = variant;
        }

        public String getVariantName() {
            return variant;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof InvalidFeatureFormat)) return false;
            InvalidFeatureFormat that = (InvalidFeatureFormat) o;
            return Objects.equals(variant, that.variant);
        }

        @Override
        public int hashCode() {
            return Objects.hash(variant);
        }
    }

    public static class IncompatibleGeneAndProteinName extends FeatureErrorValue {

        private final String geneName;
        private final String proteinName;
        private final List<String> expectedGeneNames;

        public IncompatibleGeneAndProteinName(String geneName, String proteinName, List<String> expectedGeneNames) {
            super("protein "+proteinName+" is not compatible with gene name "+geneName +" (expected gene names: "+
                    expectedGeneNames+")");

            this.geneName = geneName;
            this.proteinName = proteinName;
            this.expectedGeneNames = expectedGeneNames;
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
