package org.nextprot.api.core.domain;

import com.google.common.base.Optional;

/**
 * Databases referenced in DbXref
 *
 * Created by fnikitin on 10/12/15.
 */
public enum XRefDatabase {

    WEBINFO("WEBINFO"),
    EMBL("EMBL"),
    ENSEMBL("Ensembl"),
    COSMIC("Cosmic"),
    CLINVAR("Clinvar"),
    PIR("PIR"),
    GERMONLINE("GermOnline"),
    GENEVESTIGATOR("Genevestigator"),
    HPA("HPA"),
    GENEVISIBLE("Genevisible"),
    UNI_GENE("UniGene"),
    UCSC("UCSC"),
    INTACT("IntAct"),
    PROSITE("PROSITE"),
    HSSP("HSSP"),
    BGEE("Bgee"),
    PEPTIDE_ATLAS("PeptideAtlas"),
    SRM_ATLAS("SRMAtlas"),
    PDB("PDB"),
    TKG("TKG"),
    NIH_ARP("NIH-ARP"),
    CGH_DB("CGH-DB"),
    IFO("IFO"),
    JCRB("JCRB"),
    BRENDA("Brenda"),
    PROTEOPEDIA("PROTEOPEDIA"),
    REF_SEQ("RefSeq"),
    OBO("OBO")
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

        switch(name) {

            case "WEBINFO":
                return WEBINFO;
            case "EMBL":
                return EMBL;
            case "Ensembl":
                return ENSEMBL;
            case "Cosmic":
                return COSMIC;
            case "Clinvar":
                return CLINVAR;
            case "PIR":
                return PIR;
            case "GermOnline":
                return GERMONLINE;
            case "HPA":
                return HPA;
            case "Genevisible":
                return GENEVISIBLE;
            case "Genevestigator":
                return GENEVESTIGATOR;
            case "UniGene":
                return UNI_GENE;
            case "UCSC":
                return UCSC;
            case "IntAct":
                return INTACT;
            case "PROSITE":
                return PROSITE;
            case "HSSP":
                return HSSP;
            case "Bgee":
                return BGEE;
            case "PeptideAtlas":
                return PEPTIDE_ATLAS;
            case "SRMAtlas":
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
            case "Brenda":
                return BRENDA;
            case "PROTEOPEDIA":
                return PROTEOPEDIA;
            case "RefSeq":
                return REF_SEQ;
            case "OBO":
                return OBO;
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
