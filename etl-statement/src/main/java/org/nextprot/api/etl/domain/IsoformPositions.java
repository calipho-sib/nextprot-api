package org.nextprot.api.etl.domain;

import org.nextprot.commons.statements.TargetIsoformSet;

public class IsoformPositions {

    private Integer beginPositionOfCanonicalOrIsoSpec;
    private Integer endPositionOfCanonicalOrIsoSpec;
    private Integer masterBeginPosition;
    private Integer masterEndPosition;
    private String canonicalIsoform;
    private TargetIsoformSet targetIsoformSet;

    public Integer getBeginPositionOfCanonicalOrIsoSpec() {
        return beginPositionOfCanonicalOrIsoSpec;
    }

    public void setBeginPositionOfCanonicalOrIsoSpec(Integer beginPositionOfCanonicalOrIsoSpec) {
        this.beginPositionOfCanonicalOrIsoSpec = beginPositionOfCanonicalOrIsoSpec;
    }

    public Integer getEndPositionOfCanonicalOrIsoSpec() {
        return endPositionOfCanonicalOrIsoSpec;
    }

    public void setEndPositionOfCanonicalOrIsoSpec(Integer endPositionOfCanonicalOrIsoSpec) {
        this.endPositionOfCanonicalOrIsoSpec = endPositionOfCanonicalOrIsoSpec;
    }

    public Integer getMasterBeginPosition() {
        return masterBeginPosition;
    }

    public void setMasterBeginPosition(Integer masterBeginPosition) {
        this.masterBeginPosition = masterBeginPosition;
    }

    public Integer getMasterEndPosition() {
        return masterEndPosition;
    }

    public void setMasterEndPosition(Integer masterEndPosition) {
        this.masterEndPosition = masterEndPosition;
    }

    public String getCanonicalIsoform() {
        return canonicalIsoform;
    }

    public void setCanonicalIsoform(String canonicalIsoform) {
        this.canonicalIsoform = canonicalIsoform;
    }

    public boolean hasTargetIsoforms() {
        return targetIsoformSet != null && !targetIsoformSet.isEmpty();
    }

    public boolean hasExactPositions() {
        return beginPositionOfCanonicalOrIsoSpec != null && endPositionOfCanonicalOrIsoSpec != null
                && masterBeginPosition != null && masterEndPosition != null;
    }

    public TargetIsoformSet getTargetIsoformSet() {
        return targetIsoformSet;
    }

    public void setTargetIsoformSet(TargetIsoformSet targetIsoformSet) {
        this.targetIsoformSet = targetIsoformSet;
    }
}
