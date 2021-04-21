package org.nextprot.api.core.domain;

import java.util.ArrayList;
import java.util.List;

public class FunctionPrediction {

    // GO Function type: biological process or molecular function
    private String type;

    private String entryAC;

    private String cvTermAccession;

    private String cvName;

    private String cvTermDescription;

    private List<PredictionEvidence> evidences = new ArrayList<>();

    public FunctionPrediction(String cvTermAccession) {
        this.cvTermAccession = cvTermAccession;
    }

    public void setEntryAC(String entryAC) {
        this.entryAC = entryAC;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCvName(String cvName) {
        this.cvName = cvName;
    }

    public void setCvTermDescription(String cvTermDescription) {
        this.cvTermDescription = cvTermDescription;
    }

    public String getType() {
        return this.type;
    }

    public String getEntryAC() {
        return this.entryAC;
    }

    public String getCvTermAccession() {
        return this.cvTermAccession;
    }

    public String getCvName() {
        return this.cvName;
    }

    public String getCvTermDescription() {
        return this.cvTermDescription;
    }

    public void addEvidence(PredictionEvidence evidence) {
        this.evidences.add(evidence);
    }

    public List<PredictionEvidence> getEvidences() {
        return this.evidences;
    }
}

