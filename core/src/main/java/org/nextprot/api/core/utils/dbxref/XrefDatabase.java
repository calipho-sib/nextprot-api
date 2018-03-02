package org.nextprot.api.core.utils.dbxref;


/**
 * Database names referenced by neXtProt
 */
public enum XrefDatabase {

    BGEE("Bgee"),
    BRENDA("BRENDA"),
    CHITARS("ChiTaRS"),
    CGH_DB("CGH-DB"),
    CLINVAR("Clinvar"),
    COSMIC("Cosmic"),
    EMBL("EMBL"),
    ENSEMBL("Ensembl"),
    EXPRESSION_ATLAS("ExpressionAtlas"),
    GENEVESTIGATOR("Genevestigator"),
    GENEVISIBLE("Genevisible"),
    GERMONLINE("GermOnline"),
    HAMAP_RULE("HAMAP-Rule"),
    HPA("HPA"),
    HSSP("HSSP"),
    IFO("IFO"),
    INTACT("IntAct"),
    JCRB("JCRB"),
    LOC("LOC"),
    NIH_ARP("NIH-ARP"),
    OBO("OBO"),
    PDB("PDB"),
    PEPTIDE_ATLAS("PeptideAtlas"),
    PEROXIBASE("PeroxiBase"),
    PIR("PIR"),
    PROSITE("PROSITE"),
    PROSITE_PRORULE("PROSITE-ProRule"),
    PROTEOPEDIA("PROTEOPEDIA"),
    REF_SEQ("RefSeq"),
    RULEBASE("RuleBase"),
    SIGNOR("SIGNOR"),
    SMR("SMR"),
    SRM_ATLAS("SRMAtlas"),
    TKG("TKG"),
    UCSC("UCSC"),
    UNI_GENE("UniGene"),
    WEBINFO("WEBINFO")
    ;

    private final String name;

    XrefDatabase(String name) {

        this.name = name;
    }

    public String getName() {
        return name;
    }
}