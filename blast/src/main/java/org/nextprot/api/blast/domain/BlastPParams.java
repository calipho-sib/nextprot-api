package org.nextprot.api.blast.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.nextprot.api.blast.service.BlastProgram;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.ExceptionWithReason;

import java.util.Arrays;

/**
 * Configuration object for blastp execution
 */
public class BlastPParams extends BlastProgram.Params {

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

    public BlastPParams(String blastPBinPath, String nextprotBlastDbPath) {

        super(blastPBinPath, nextprotBlastDbPath);
    }

    public void setFields(String matrix, Double eValue, Integer gapOpen, Integer gapExtend) throws ExceptionWithReason {

        if (matrix != null) {
            if (Matrix.hasMatrix(matrix))
                setMatrix(BlastPParams.Matrix.valueOf(matrix));
            else {
                throw ExceptionWithReason.withReason("unknown substitution matrix", matrix);
            }
        }
        setEvalue(eValue);
        setGapOpen(gapOpen);
        setGapExtend(gapExtend);
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

    @JsonIgnore
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

    public static class BlastPIsoformParams extends BlastPParams {

        private static final String ISOFORM_REX_EXP= "^NX_[^-]+-\\d+$";

        private String isoformAccession;
        private Integer querySeqBegin;
        private Integer querySeqEnd;

        public BlastPIsoformParams(String blastPBinPath, String nextprotBlastDbPath) {

            super(blastPBinPath, nextprotBlastDbPath);
        }

        public String getIsoformAccession() {
            return isoformAccession;
        }

        public void setIsoformAccession(String isoformAccession) {

            if (isoformAccession == null || !isoformAccession.matches(ISOFORM_REX_EXP)) {
                throw new NextProtException(isoformAccession+": invalid isoform accession (format: "+ISOFORM_REX_EXP+")");
            }

            this.isoformAccession = isoformAccession;
        }

        public Integer getQuerySeqBegin() {
            return querySeqBegin;
        }

        public void setQuerySeqBegin(Integer querySeqBegin) {
            this.querySeqBegin = querySeqBegin;
        }

        public Integer getQuerySeqEnd() {
            return querySeqEnd;
        }

        public void setQuerySeqEnd(Integer querySeqEnd) {
            this.querySeqEnd = querySeqEnd;
        }

        @JsonIgnore
        public int getBeginPos() {
            return (querySeqBegin != null) ? querySeqBegin : 1;
        }

        @JsonIgnore
        public int getEndPos() {
            return (querySeqEnd != null) ? querySeqEnd : getSequenceLen();
        }

        public int calcQuerySeqLength() {


            return getEndPos() - getBeginPos() + 1;
        }

        public void validateSequencePositions() throws ExceptionWithReason {

            if (getSequenceQuery() == null) {

                throw ExceptionWithReason.withMessage("missing query sequence");
            }

            if (querySeqBegin == null && querySeqEnd != null || querySeqBegin != null && querySeqEnd == null) {

                throw ExceptionWithReason.withMessage("begin: "+querySeqBegin+", end: "+querySeqEnd+": sequence positions should be both defined or undefined");
            }

            // both positions are defined
            if (querySeqBegin != null) {

                swapPositionsIfNeeded();

                // check positions
                if (querySeqBegin < 1 || querySeqBegin > getSequenceLen()) {

                    throw ExceptionWithReason.withReason("first sequence position ", querySeqBegin + " is out of bound (should be > 0 and <= " + getSequenceLen() + ")");
                }
                if (querySeqEnd < 1 || querySeqEnd > getSequenceLen()) {

                    throw ExceptionWithReason.withReason("last sequence position ", querySeqEnd + " is out of bound (should be > 0 and <= " + getSequenceLen() + ")");
                }
            }
        }

        private void swapPositionsIfNeeded() {

            if (querySeqBegin > querySeqEnd) {

                int tmp = querySeqBegin;
                querySeqBegin = querySeqEnd;
                querySeqEnd = tmp;
            }
        }
    }
}
