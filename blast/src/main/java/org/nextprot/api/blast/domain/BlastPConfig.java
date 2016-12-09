package org.nextprot.api.blast.domain;


import org.nextprot.api.blast.service.BlastProgram;
import org.nextprot.api.commons.utils.ExceptionWithReason;

import java.util.Arrays;

/**
 * Configuration object for blastp execution
 */
public class BlastPConfig extends BlastProgram.Config {

    /**
     * Amino acid substitution matrices (see also https://en.wikipedia.org/wiki/Substitution_matrix#Log-odds_matrices)
     */
    public enum Matrix {
        BLOSUM45, BLOSUM50, BLOSUM62, BLOSUM80, BLOSUM90, PAM250, PAM30, PAM70;

        public static boolean hasMatrix(String matrix) {

            return Arrays.stream(Matrix.values()).anyMatch(value -> matrix.equalsIgnoreCase(value.toString()));
        }
    }

    private String queryHeader;
    private String sequenceQuery;
    private Integer sequenceLen;
    private Matrix matrix;
    private Double evalue;
    private Integer gapOpen;
    private Integer gapExtend;

    public BlastPConfig(String blastPBinPath, String nextprotBlastDbPath) {

        super(blastPBinPath, nextprotBlastDbPath);
    }

    public static BlastPConfig all(String blastBinPath, String blastDbPath, String matrix, Double eValue, Integer gapOpen, Integer gapExtend) throws ExceptionWithReason {

        BlastPConfig config = new BlastPConfig(blastBinPath, blastDbPath);

        if (matrix != null) {
            if (Matrix.hasMatrix(matrix))
                config.setMatrix(BlastPConfig.Matrix.valueOf(matrix));
            else {
                throw ExceptionWithReason.withReason("unknown substitution matrix", matrix);
            }
        }
        config.setEvalue(eValue);
        config.setGapOpen(gapOpen);
        config.setGapExtend(gapExtend);

        return config;
    }

    public String getQueryHeader() {
        return queryHeader;
    }

    public void setQueryHeader(String queryHeader) {
        this.queryHeader = queryHeader;
    }

    public String getSequenceQuery() {
        return sequenceQuery;
    }

    public void setSequenceQuery(String sequenceQuery) {
        this.sequenceQuery = sequenceQuery;
        this.sequenceLen = sequenceQuery.length();
    }

    public int getSequenceLen() {
        return sequenceLen;
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

    public void setEvalue(Double evalue) {
        this.evalue = evalue;
    }

    public Integer getGapOpen() {
        return gapOpen;
    }

    public void setGapOpen(Integer gapOpen) {
        this.gapOpen = gapOpen;
    }

    public Integer getGapExtend() {
        return gapExtend;
    }

    public void setGapExtend(Integer gapExtend) {
        this.gapExtend = gapExtend;
    }
}
