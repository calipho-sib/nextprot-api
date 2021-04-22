package org.nextprot.api.core.domain;

import java.util.ArrayList;
import java.util.List;

public class PredictionEvidence {

    private String statementID;

    private String evidenceCodeAC;

    private String evidenceCodeName;

    private String publicationAc;

    private String publicationDatabaseName;

    private List<String> userOrcIDs = new ArrayList<>();

    private boolean userHidden;

    public PredictionEvidence(String evidenceCodeAC) {
        this.evidenceCodeAC = evidenceCodeAC;
    }

    public void setStatementID(String statementID) {
        this.statementID = statementID;
    }

    public void setPublicationAc(String publicationAccession) {
        this.publicationAc = publicationAccession;
    }

    public void setPublicationDatabaseName(String publicationDatabaseName) {
        this.publicationDatabaseName = publicationDatabaseName;
    }

    public void setEvidenceCodeName(String ecoCodeName) {
        this.evidenceCodeName = ecoCodeName;
    }

    public void adduserOrcIDs(String orcID) {
        this.userOrcIDs.add(orcID);
    }

    public void setUserHidden(boolean userHidden) {
        this.userHidden = userHidden;
    }

    public String getStatementID() {
        return this.statementID;
    }

    public String getPublicationAc() {
        return this.publicationAc;
    }

    public String getPublicationDatabaseName() {
        return this.publicationDatabaseName;
    }

    public String getEvidenceCodeAC() {
        return this.evidenceCodeAC;
    }

    public String getEvidenceCodeName() {
        return this.evidenceCodeName;
    }

    public List<String> getUserOrcIDs() {
        return this.userOrcIDs;
    }

    public boolean getUserHidden() {
        return this.userHidden;
    }
}