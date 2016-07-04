package com.nextprot.api.isoform.mapper.domain;

import org.nextprot.api.commons.bio.AminoAcidCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A mapping error
 */
public abstract class MappedIsoformsFeatureError extends MappedIsoformsFeatureResult {

    private final ErrorValue error;

    public MappedIsoformsFeatureError(Query query) {
        super(query);
        error = new ErrorValue();
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    public ErrorValue getError() {

        return error;
    }

    public static class ErrorValue {

        private final Map<String, Object> causes = new HashMap<>();
        private String message;

        public Map<String, Object> getCauses() {
            return causes;
        }

        public Object getCause(String key) {
            return causes.get(key);
        }

        void addCause(String key, Object value) {

            causes.put(key, value);
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ErrorValue)) return false;
            ErrorValue value = (ErrorValue) o;
            return Objects.equals(causes, value.causes) &&
                    Objects.equals(message, value.message);
        }

        @Override
        public int hashCode() {
            return Objects.hash(causes, message);
        }
    }

    public static class InvalidFeaturePosition extends MappedIsoformsFeatureError {

        static final String SEQUENCE_POS = "sequence-position";

        public InvalidFeaturePosition(Query query, int position) {

            super(query);

            getError().setMessage("invalid feature position: position "+position+" is out of bound in sequence of isoform "+query.getAccession());
            getError().addCause(SEQUENCE_POS, position);
        }

        public int getIsoformSequencePosition() {
            return (Integer) getError().getCause(InvalidFeaturePosition.SEQUENCE_POS);
        }
    }

    public static class InvalidFeatureAminoAcid extends MappedIsoformsFeatureError {

        private static final String SEQUENCE_AAS = "sequence-amino-acids";
        private static final String FEATURE_AAS = "feature-amino-acids";

        public InvalidFeatureAminoAcid(Query query, int isoformSequencePosition,
                                       AminoAcidCode[] sequenceAminoAcidCodes, AminoAcidCode[] featureAminoAcidCodes) {
            super(query);

            getError().addCause(SEQUENCE_AAS, AminoAcidCode.formatAminoAcidCode(AminoAcidCode.AACodeType.THREE_LETTER, sequenceAminoAcidCodes));
            getError().addCause(FEATURE_AAS, AminoAcidCode.formatAminoAcidCode(AminoAcidCode.AACodeType.THREE_LETTER, featureAminoAcidCodes));
            getError().addCause(InvalidFeaturePosition.SEQUENCE_POS, isoformSequencePosition);

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

        public String getFeatureAminoAcids() {
            return (String) getError().getCause(FEATURE_AAS);
        }

        public String getSequenceAminoAcids() {
            return (String) getError().getCause(SEQUENCE_AAS);
        }

        public int getIsoformSequencePosition() {
            return (Integer) getError().getCause(InvalidFeaturePosition.SEQUENCE_POS);
        }
    }

    public static class InvalidFeatureFormat extends MappedIsoformsFeatureError {

        public InvalidFeatureFormat(Query query) {

            super(query);

            getError().setMessage("invalid feature format: "+query.getFeature());
        }
    }

    public static class IncompatibleGeneAndProteinName extends MappedIsoformsFeatureError {

        private static final String GENE_NAME = "gene-name";
        private static final String EXPECTED_GENE_NAMES = "expected-gene-names";

        public IncompatibleGeneAndProteinName(Query query, String geneName, List<String> expectedGeneNames) {

            super(query);

            getError().addCause(GENE_NAME, geneName);
            getError().addCause(EXPECTED_GENE_NAMES, expectedGeneNames);
            getError().setMessage("gene/protein incompatibility: protein "+query.getAccession()+" is not compatible with gene "+geneName +" (expected genes: "+ expectedGeneNames+")");
        }

        public String getGeneName() {
            return (String) getError().getCause(GENE_NAME);
        }

        public List<String> getExpectedGeneNames() {
            //noinspection unchecked
            return (List<String>) getError().getCause(EXPECTED_GENE_NAMES);
        }
    }
}
