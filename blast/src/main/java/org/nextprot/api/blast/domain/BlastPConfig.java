package org.nextprot.api.blast.domain;

import java.io.Serializable;


public class BlastPConfig implements Serializable {

    public enum Matrix {
        BLOSUM45, BLOSUM50, BLOSUM62, BLOSUM80, BLOSUM90, PAM250, PAM30, PAM70
    }

    private final String blastDirPath;
    private final String nextprotDatabasePath;
    private boolean isDebugMode = false;

    private Matrix matrix = Matrix.BLOSUM62;
    private Double expect = 10.;
    private Integer gapOpen = 11;
    private Integer gapExtend = 1;
    private String filter = "F";

    public BlastPConfig(String blastDirPath, String nextprotDatabasePath) {

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

    public Double getExpect() {
        return expect;
    }

    public void setExpect(Double expect) {
        this.expect = expect;
    }

    public int getGapOpen() {
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

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }
}
