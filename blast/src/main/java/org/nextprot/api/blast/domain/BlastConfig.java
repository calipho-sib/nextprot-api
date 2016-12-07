package org.nextprot.api.blast.domain;

import java.io.Serializable;


/**
 * Configuration object for blastp
 */
public class BlastConfig implements Serializable {

    /**
     * Amino acid substitution matrices (see also https://en.wikipedia.org/wiki/Substitution_matrix#Log-odds_matrices)
     */
    public enum Matrix {
        BLOSUM45, BLOSUM50, BLOSUM62, BLOSUM80, BLOSUM90, PAM250, PAM30, PAM70
    }

    private final String blastDirPath;
    private final String nextprotDatabasePath;
    private boolean isDebugMode = false;

    private Matrix matrix;
    private Double evalue;
    private Integer gapOpen;
    private Integer gapExtend;

    public BlastConfig(String blastDirPath, String nextprotDatabasePath) {

        this.blastDirPath = blastDirPath;
        this.nextprotDatabasePath = nextprotDatabasePath;
    }

    public String getBlastDirPath() {
        return blastDirPath;
    }

    public String getNextprotDatabasePath() {
        return nextprotDatabasePath;
    }

    public boolean isDebugMode() {
        return isDebugMode;
    }

    public void setDebugMode(boolean debugMode) {
        isDebugMode = debugMode;
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
