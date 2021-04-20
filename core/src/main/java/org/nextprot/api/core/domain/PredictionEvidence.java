package org.nextprot.api.core.domain;

import java.util.ArrayList;
import java.util.List;

public class PredictionEvidence {

    private String statementID;

    private String ecoCodeAccession;

    private String ecoCodeName;

    private String publicationAccession;

    private String publicationDatabaseName;

    private List<String> orcIDs = new ArrayList<>();

    public PredictionEvidence(String ecoCodeAccession) {
        this.ecoCodeAccession = ecoCodeAccession;
    }

    public void setStatementID(String statementID) {
        this.statementID = statementID;
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

    public String getEcoCodeAccession() {
        return this.ecoCodeAccession;
    }

    public String getEcoCodeName() {
        return this.ecoCodeName;
    }

    public List<String> getOrcIDs() {
        return this.orcIDs;
    }
}