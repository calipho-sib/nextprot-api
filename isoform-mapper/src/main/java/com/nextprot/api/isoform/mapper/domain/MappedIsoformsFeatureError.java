package com.nextprot.api.isoform.mapper.domain;

import java.io.Serializable;
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

        public UnexpectedAminoAcids(String expectedAas, String observedAas) {
            super("expected="+expectedAas+", observed="+observedAas);

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

        public IncompatibleGeneAndProteinName(String geneName, String proteinName) {
            super("inconsistent gene and protein names");

            this.geneName = geneName;
            this.proteinName = proteinName;
        }

        public String getGeneName() {
            return geneName;
        }

        public String getProteinName() {
            return proteinName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof IncompatibleGeneAndProteinName)) return false;
            IncompatibleGeneAndProteinName that = (IncompatibleGeneAndProteinName) o;
            return Objects.equals(geneName, that.geneName) &&
                    Objects.equals(proteinName, that.proteinName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(geneName, proteinName);
        }
    }
}
