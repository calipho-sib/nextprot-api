package org.nextprot.api.core.domain;

import org.nextprot.api.core.service.TerminologyService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class FunctionPrediction {

    // GO Function type: biological process or molecular function
    private String type;

    private String cvTermAccession;

    private String cvName;

    private String cvTermDescription;

    private List<PredictionEvidence> evidences = new ArrayList<>();

    public FunctionPrediction(String cvTermAccession) {
        this.cvTermAccession = cvTermAccession;
    }

    public void setCvName(String cvName) {
        this.cvName = cvName;
    }

    public void setCvTermDescription(String cvTermDescription) {
        this.cvTermDescription = cvTermDescription;
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

