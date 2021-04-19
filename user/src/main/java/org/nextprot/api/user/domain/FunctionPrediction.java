package org.nextprot.api.user.domain;

import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.service.TerminologyService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class FunctionPrediction {

    @Autowired
    TerminologyService terminologyService;

    // GO Function type: biological process or molecular function
    private String type;

    private String cvTermAccession;

    private String cvName;

    private String cvTermDescription;

    private List<PredictionEvidence> evidences;

    public FunctionPrediction(String cvTermAccession) {
        this.cvTermAccession = cvTermAccession;

        // Populates cvterm data
        CvTerm cvTerm = terminologyService.findCvTermByAccessionOrThrowRuntimeException(cvTermAccession);
        this.cvName = cvTerm.getName();
        this.cvTermDescription = cvTerm.getDescription();
    }

    public void addEvidence(String ecoCode, String orcIDs) {
        PredictionEvidence predictionEvidence = new PredictionEvidence(ecoCode);

        CvTerm ecoTerm = terminologyService.findCvTermByAccession(ecoCode);
        predictionEvidence.setEcoCodeName(ecoTerm.getName());

        StringTokenizer tokenizer = new StringTokenizer(orcIDs,",");
        while(tokenizer.hasMoreTokens()) {
            predictionEvidence.addOcrIDs(tokenizer.nextToken());
        }
    }
}

class PredictionEvidence {

    private String statementID;

    private String ecoCodeAccession;

    private String ecoCodeName;

    private String publicationAccession;

    private String publicationDatabaseName;

    private List<String> orcIDs = new ArrayList<>();

    public void setStatementID(String statementID) {
        this.statementID = statementID;
    }

    public PredictionEvidence(String ecoCodeAccession) {
        this.ecoCodeAccession = ecoCodeAccession;
    }

    public void setPublicationAccession(String publicationAccession) {
        this.publicationAccession = publicationAccession;
    }

    public void setPublicationDatabaseName(String publicationDatabaseName) {
        this.publicationDatabaseName = publicationDatabaseName;
    }

    public void setEcoCodeName(String ecoCodeName) {
        this.ecoCodeName = ecoCodeName;
    }

    public void addOcrIDs(String orcID) {
        this.orcIDs.add(orcID);
    }

    public String getStatementID() {
        return this.statementID;
    }

    public String getPublicationAccession() {
        return this.publicationAccession;
    }

    public String getPublicationDatabaseName() {
        return this.publicationDatabaseName;
    }

    public List<String> getOrcIDs() {
        return this.orcIDs;
    }
}
