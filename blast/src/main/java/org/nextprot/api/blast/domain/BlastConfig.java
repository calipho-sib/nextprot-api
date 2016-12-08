package org.nextprot.api.blast.domain;

import java.io.Serializable;
import java.util.Objects;


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

    private final String nextprotBlastDbPath;
    private boolean isDebugMode = false;
    private String blastBinPath;
    private String makeBlastDbBinPath;

    private Matrix matrix;
    private Double evalue;
    private Integer gapOpen;
    private Integer gapExtend;

    public BlastConfig(String nextprotBlastDbPath) {

        Objects.requireNonNull(nextprotBlastDbPath, "nextprot blast db path is missing");

        this.nextprotBlastDbPath = nextprotBlastDbPath;
    }

    public static BlastConfig newBlastPConfig(String blastBinPath, String blastDbPath, String matrix, Double eValue, Integer gapOpen, Integer gapExtend) {

        BlastConfig config = new BlastConfig(blastDbPath);
        config.setBlastBinPath(blastBinPath);

        if (matrix != null)
            config.setMatrix(BlastConfig.Matrix.valueOf(matrix));
        config.setEvalue(eValue);
        config.setGapOpen(gapOpen);
        config.setGapExtend(gapExtend);

        return config;
    }

    public String getBlastBinPath() {
        return blastBinPath;
    }

    public void setBlastBinPath(String blastBinPath) {
        this.blastBinPath = blastBinPath;
    }

    public String getMakeBlastDbBinPath() {
        return makeBlastDbBinPath;
    }

    public void setMakeBlastDbBinPath(String makeBlastDbBinPath) {
        this.makeBlastDbBinPath = makeBlastDbBinPath;
    }

    public String getNextprotBlastDbPath() {
        return nextprotBlastDbPath;
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
