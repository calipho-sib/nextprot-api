package org.nextprot.api.core.domain;

import java.util.ArrayList;
import java.util.List;

public class FunctionPrediction {

    // GO Function type: biological process or molecular function
    private String type;

    private String entryAC;

    private String cvTermAccessionCode;

    private String cvTermName;

    private String cvTermDescription;

    private List<PredictionEvidence> evidences = new ArrayList<>();

    public FunctionPrediction(String cvTermAccession) {
        this.cvTermAccessionCode = cvTermAccession;
    }

    public void setEntryAC(String entryAC) {
        this.entryAC = entryAC;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCvTermName(String cvTermName) {
        this.cvTermName = cvTermName;
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
        return this.cvTermAccessionCode;
    }

    public String getCvTermName() {
        return this.cvTermName;
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

