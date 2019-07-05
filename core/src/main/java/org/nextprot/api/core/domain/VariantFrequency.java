package org.nextprot.api.core.domain;

public class VariantFrequency {

    /*
    Data source
     */
    private String source;

    private int alleleCount;

    private int allelNumber;

    private double allelFrequency;

    public void setSource(String source) {
        this.source = source;
    }

    public void setAlleleCount(int alleleCount) {
        this.alleleCount = alleleCount;
    }

    public void setAllelNumber(int allelNumber) {
        this.allelNumber = allelNumber;
    }

    public void setAllelFrequency(int allelFrequency) {
        this.allelFrequency = allelFrequency;
    }

    public String getSource() {
        return source;
    }

    public int getAlleleCount() {
        return alleleCount;
    }

    public int getAllelNumber() {
        return allelNumber;
    }

    public double getAllelFrequency() {
        return allelFrequency;
    }

}
