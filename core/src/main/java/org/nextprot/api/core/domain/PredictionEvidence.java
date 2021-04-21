package org.nextprot.api.core.domain;

import java.util.ArrayList;
import java.util.List;

public class PredictionEvidence {

    private String statementID;

    private String evidenceCodeAC;

    private String evidenceCodeName;

    private String publicationAc;

    private String publicationDatabaseName;

    private List<String> orcIDs = new ArrayList<>();

    public PredictionEvidence(String ecoCodeAccession) {
        this.evidenceCodeAC = ecoCodeAccession;
    }

    public void setStatementID(String statementID) {
        this.statementID = statementID;
    }

    public void setPublicationAccession(String publicationAccession) {
        this.publicationAc = publicationAccession;
    }

    public void setPublicationDatabaseName(String publicationDatabaseName) {
        this.publicationDatabaseName = publicationDatabaseName;
    }

    public void setEcoCodeName(String ecoCodeName) {
        this.evidenceCodeName = ecoCodeName;
    }

    public void addOcrIDs(String orcID) {
        this.orcIDs.add(orcID);
    }

    public String getStatementID() {
        return this.statementID;
    }

    public String getPublicationAccession() {
        return this.publicationAc;
    }

    public String getPublicationDatabaseName() {
        return this.publicationDatabaseName;
    }

    public String getEcoCodeAccession() {
        return this.evidenceCodeAC;
    }

    public String getEcoCodeName() {
        return this.evidenceCodeName;
    }

    public List<String> getOrcIDs() {
        return this.orcIDs;
    }
}