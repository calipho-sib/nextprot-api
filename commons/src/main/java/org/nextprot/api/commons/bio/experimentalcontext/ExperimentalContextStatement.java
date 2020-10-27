package org.nextprot.api.commons.bio.experimentalcontext;

public class ExperimentalContextStatement {
    private String tissueAC;
    private String developmentStageAC;
    private String detectionMethodAC;

    public void setTissueAC(String tissueAC) {
        this.tissueAC = tissueAC;
    }

    public void setDevelopmentStageAC(String developmentStageAC) {
        this.developmentStageAC = developmentStageAC;
    }

    public void setDetectionMethodAC(String detectionMethodAC) {
        this.detectionMethodAC = detectionMethodAC;
    }

    public String getTissueAC() {
        return this.tissueAC;
    }

    public String getDevelopmentStageAC() {
        return this.developmentStageAC;
    }

    public String getDetectionMethodAC() {
        return this.detectionMethodAC;
    }
}
