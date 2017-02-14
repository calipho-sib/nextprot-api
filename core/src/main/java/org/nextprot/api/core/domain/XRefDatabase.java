package org.nextprot.api.core.domain;

import com.google.common.base.Optional;

/**
 * Databases referenced in DbXref
 *
 * Created by fnikitin on 10/12/15.
 */
public enum XRefDatabase {

    BGEE("Bgee"),
    BRENDA("Brenda"),
    CGH_DB("CGH-DB"),
    CLINVAR("Clinvar"),
    COSMIC("Cosmic"),
    EMBL("EMBL"),
    ENSEMBL("Ensembl"),
    GENEVESTIGATOR("Genevestigator"),
    GENEVISIBLE("Genevisible"),
    GERMONLINE("GermOnline"),
    HPA("HPA"),
    HSSP("HSSP"),
    IFO("IFO"),
    INTACT("IntAct"),
    JCRB("JCRB"),
    NIH_ARP("NIH-ARP"),
    OBO("OBO"),
    PDB("PDB"),
    PEPTIDE_ATLAS("PeptideAtlas"),
    PEROXIBASE("PeroxiBase"),
    PIR("PIR"),
    PROSITE("PROSITE"),
    PROTEOPEDIA("PROTEOPEDIA"),
    REF_SEQ("RefSeq"),
    SMR("SMR"),
    SRM_ATLAS("SRMAtlas"),
    TKG("TKG"),
    UCSC("UCSC"),
    UNI_GENE("UniGene"),
    WEBINFO("WEBINFO"),
    ;

    private final String name;

    XRefDatabase(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Get a XRefDatabase enum from name
     * @param name the enum name
     * @return XRefDatabase enum or null if not found
     */
    public static XRefDatabase valueOfDbName(String name) {

        switch(name.toUpperCase()) {

            case "WEBINFO":
                return WEBINFO;
            case "EMBL":
                return EMBL;
            case "ENSEMBL":
                return ENSEMBL;
            case "COSMIC":
                return COSMIC;
            case "CLINVAR":
                return CLINVAR;
            case "PIR":
                return PIR;
            case "GERMONLINE":
                return GERMONLINE;
            case "HPA":
                return HPA;
            case "GENEVISIBLE":
                return GENEVISIBLE;
            case "GENEVESTIGATOR":
                return GENEVESTIGATOR;
            case "UNIGENE":
                return UNI_GENE;
            case "UCSC":
                return UCSC;
            case "INTACT":
                return INTACT;
            case "PROSITE":
                return PROSITE;
            case "HSSP":
                return HSSP;
            case "BGEE":
                return BGEE;
            case "PEPTIDEATLAS":
                return PEPTIDE_ATLAS;
            case "SRMATLAS":
                return SRM_ATLAS;
            case "PDB":
                return PDB;
            case "TKG":
                return TKG;
            case "NIH-ARP":
                return NIH_ARP;
            case "CGH-DB":
                return CGH_DB;
            case "IFO":
                return IFO;
            case "JCRB":
                return JCRB;
            case "BRENDA":
                return BRENDA;
            case "PROTEOPEDIA":
                return PROTEOPEDIA;
            case "REFSEQ":
                return REF_SEQ;
            case "OBO":
                return OBO;
            case "SMR":
                return SMR;
            case "PEROXIBASE":
                return PEROXIBASE;
            default:
                return null;
        }
    }

    /**
     * Get an optional XRefDatabas enum from name
     * @param name the enum name
     * @return XRefDatabase enum of absent if not found
     */
    public static Optional<XRefDatabase> optionalValueOfDbName(String name) {

        return Optional.fromNullable(valueOfDbName(name));
    }
}
