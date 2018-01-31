package org.nextprot.api.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public class ProteinExistences implements Serializable {

    private static final long serialVersionUID = 1L;

    private String entryAccession;
    private ProteinExistenceInferred proteinExistenceInferred;
    private ProteinExistence otherProteinExistenceUniprot;
    private ProteinExistence otherProteinExistenceNexprot1;

    public String getEntryAccession() {
        return entryAccession;
    }

    public void setEntryAccession(String entryAccession) {
        this.entryAccession = entryAccession;
    }

    public ProteinExistenceInferred getProteinExistenceInferred() {
        return proteinExistenceInferred;
    }

    public void setProteinExistenceInferred(ProteinExistenceInferred proteinExistenceInferred) {
        this.proteinExistenceInferred = proteinExistenceInferred;
    }

    public ProteinExistence getOtherProteinExistenceUniprot() {
        return otherProteinExistenceUniprot;
    }

    public void setOtherProteinExistenceUniprot(ProteinExistence otherProteinExistenceUniprot) {
        this.otherProteinExistenceUniprot = otherProteinExistenceUniprot;
    }

    public ProteinExistence getOtherProteinExistenceNexprot1() {
        return otherProteinExistenceNexprot1;
    }

    public void setOtherProteinExistenceNexprot1(ProteinExistence otherProteinExistenceNexprot1) {
        this.otherProteinExistenceNexprot1 = otherProteinExistenceNexprot1;
    }

    @JsonIgnore
    public ProteinExistence getProteinExistence() {

        return proteinExistenceInferred.getProteinExistence();
    }

    public ProteinExistence getProteinExistence(ProteinExistence.Source source) {

        switch (source) {

            case PROTEIN_EXISTENCE_UNIPROT:
                return otherProteinExistenceUniprot;
            case PROTEIN_EXISTENCE_NEXTPROT1:
                return otherProteinExistenceNexprot1;
            default:
                return proteinExistenceInferred.getProteinExistence();
        }
    }
}
