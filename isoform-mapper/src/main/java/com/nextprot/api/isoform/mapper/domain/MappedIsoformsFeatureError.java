package com.nextprot.api.isoform.mapper.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * A mapping result with specified error
 */
public class MappedIsoformsFeatureError extends MappedIsoformsFeatureResult {

    private ErrorValue value;

    public MappedIsoformsFeatureError(Query query) {
        super(query);
    }

    public void setErrorValue(ErrorValue value) {

        this.value = value;
    }

    public ErrorValue getError() {
        return value;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    public static abstract class ErrorValue implements Serializable {

        private static final long serialVersionUID = 1L;

        private final String message;

        public ErrorValue(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class InvalidPosition extends ErrorValue {

        private final String isoformAccession;
        private final int position;

        public InvalidPosition(String isoformAccession, int position) {
            super("invalid position "+position+" on sequence of isoform "+isoformAccession);

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
            if (!(o instanceof InvalidPosition)) return false;
            InvalidPosition that = (InvalidPosition) o;
            return position == that.position &&
                    Objects.equals(isoformAccession, that.isoformAccession);
        }

        @Override
        public int hashCode() {
            return Objects.hash(isoformAccession, position);
        }
    }

    public static class UnexpectedAminoAcids extends ErrorValue {

        private final String expectedAas;
        private final String observedAas;

        public UnexpectedAminoAcids(String observedAas, String expectedAas) {
            super("unexpected amino-acid(s) "+observedAas+" (expected="+expectedAas+")");

            this.observedAas = observedAas;
            this.expectedAas = expectedAas;
        }

        public String getExpectedAas() {
            return expectedAas;
        }

        public String getObservedAas() {
            return observedAas;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof UnexpectedAminoAcids)) return false;
            UnexpectedAminoAcids that = (UnexpectedAminoAcids) o;
            return Objects.equals(expectedAas, that.expectedAas) &&
                    Objects.equals(observedAas, that.observedAas);
        }

        @Override
        public int hashCode() {
            return Objects.hash(expectedAas, observedAas);
        }
    }

    public static class InvalidVariantName extends ErrorValue {

        private final String variant;

        public InvalidVariantName(String variant) {
            super("invalid variant name "+variant);

            this.variant = variant;
        }

        public String getVariantName() {
            return variant;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof InvalidVariantName)) return false;
            InvalidVariantName that = (InvalidVariantName) o;
            return Objects.equals(variant, that.variant);
        }

        @Override
        public int hashCode() {
            return Objects.hash(variant);
        }
    }

    public static class IncompatibleGeneAndProteinName extends ErrorValue {

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
