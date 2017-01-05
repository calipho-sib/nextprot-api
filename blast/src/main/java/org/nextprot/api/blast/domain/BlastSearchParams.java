package org.nextprot.api.blast.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.nextprot.api.commons.utils.ExceptionWithReason;

import java.io.Serializable;
import java.util.Arrays;

public class BlastSearchParams implements Serializable {

    /**
     * Amino acid substitution matrices (see also https://en.wikipedia.org/wiki/Substitution_matrix#Log-odds_matrices)
     */
    public enum Matrix {
        BLOSUM45, BLOSUM50, BLOSUM62, BLOSUM80, BLOSUM90, PAM250, PAM30, PAM70;

        public static boolean hasMatrix(String matrix) {

            return Arrays.stream(Matrix.values()).anyMatch(value -> matrix.equalsIgnoreCase(value.toString()));
        }
    }

    private Matrix matrix;
    @JsonProperty("expect")
    private Double evalue;
    @JsonProperty("gap_open")
    private Integer gapOpen;
    @JsonProperty("gap_extend")
    private Integer gapExtend;

    public static BlastSearchParams valueOf(String matrix, Double eValue, Integer gapOpen, Integer gapExtend) throws ExceptionWithReason {

        BlastSearchParams params = new BlastSearchParams();

        if (matrix != null && !matrix.isEmpty()) {
            if (Matrix.hasMatrix(matrix))
                params.setMatrix(Matrix.valueOf(matrix));
            else {
                throw ExceptionWithReason.withReason("invalid substitution matrix parameter (valid matrices: "
                        + Arrays.asList(Matrix.values()) + ")", matrix);
            }
        }

        params.setEvalue(eValue);
        params.setGapOpen(gapOpen);
        params.setGapExtend(gapExtend);

        return params;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public Double getEvalue() {
        return evalue;
    }

    public void setEvalue(Double evalue) throws ExceptionWithReason {

        if (evalue != null && evalue <= 0) {
            throw ExceptionWithReason.withReason("invalid evalue parameter (should be > 0)", evalue.toString());
        }
        this.evalue = evalue;
    }

    public Integer getGapOpen() {
        return gapOpen;
    }

    public void setGapOpen(Integer gapOpen) throws ExceptionWithReason {

        if (gapOpen != null && gapOpen < 0) {
            throw ExceptionWithReason.withReason("invalid gapopen parameter (should be >= 0)", gapOpen.toString());
        }

        this.gapOpen = gapOpen;
    }

    public Integer getGapExtend() {
        return gapExtend;
    }

    public void setGapExtend(Integer gapExtend) throws ExceptionWithReason {

        if (gapExtend != null && gapExtend < 0) {
            throw ExceptionWithReason.withReason("invalid gapextend parameter (should be >= 0)", gapOpen.toString());
        }

        this.gapExtend = gapExtend;
    }
}
