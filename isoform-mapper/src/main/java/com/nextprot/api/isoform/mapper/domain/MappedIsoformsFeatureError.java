package com.nextprot.api.isoform.mapper.domain;

import java.io.Serializable;
import java.util.Objects;

/**
 {
     "query": {
     "accession": "NX_P01308",
     "is-canonical": true,
     "feature": "SCN11A-p.Leu1158Pro",
     "feature-type": "VARIANT",
     "propagate": false
 },
 "success": false,
 "error": {
     "message": "Invalid ..."
     "type": "INVALID_POSITION(pos)" or "UNEXPECTED_AA(expected, observed)"
    }
 }
 */
public class MappedIsoformsFeatureError extends MappedIsoformsFeatureResult {

    private ErrorValue value;

    public MappedIsoformsFeatureError(Query query) {
        super(query);
    }

    public void setErrorValue(ErrorValue value) {

        this.value = value;
    }

    public ErrorValue getErrorValue() {
        return value;
    }

    @Override
    protected String getContentName() {
        return "error";
    }

    @Override
    protected Object getContentValue() {
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
}
