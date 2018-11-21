package org.nextprot.api.core.service.dbxref;


import org.nextprot.api.commons.utils.EnumConstantDictionary;
import org.nextprot.api.commons.utils.EnumDictionarySupplier;

import java.util.Map;


/**
 * Database names referenced by neXtProt
 */
public enum XrefDatabase implements EnumDictionarySupplier<XrefDatabase> {

    ALLERGOME("Allergome"), ANTIBODYPEDIA("Antibodypedia"),
    BGEE("Bgee"), BINDING_DB("BindingDB"), BIO_CYC("BioCyc"), BIO_GRID("BioGrid"), BIO_MUTA("BioMuta"), BRENDA,
    CARBONYL_DB("CarbonylDB"), CAZY("CAZy"), CCDS, CDD, CELLOSAURUS("Cellosaurus"), CH_EMBL("ChEMBL"), CHITARS("ChiTaRS"), CGH_DB("CGH-DB"), CLEAN_EX("CleanEx"), COMPLEX_PORTAL("ComplexPortal"),
    CLINVAR("Clinvar"), CORUM, COSMIC("Cosmic"), CTD,
    DB_SNP("dbSNP"), DEPOD, DIP, DIS_GE_NET("DisGeNET"), DISPROT("DisProt"), DMDM, DNASU, DOI, DOSAC_COBS_2DPAGE("DOSAC-COBS-2DPAGE"),
    ECO, EGG_NOG("eggNOG"), ELM, EMBL, ENSEMBL("Ensembl"), ENSG, EPD, ESTHER, EU_PATH_DB("EuPathDB"),
    EVOLUTIONARY_TRACE("EvolutionaryTrace"), EXPRESSION_ATLAS("ExpressionAtlas"), EVOC("eVOC"),
    GENE_3D("Gene3D"), GENE_CARDS("GeneCards"), GENE_ID("GeneID"), GENE_REVIEWS("GeneReviews"), GENE_TREE("GeneTree"),
    GENEVESTIGATOR("Genevestigator"), GENEVISIBLE("Genevisible"), GENE_WIKI("GeneWiki"), GENOME_RNA_I("GenomeRNAi"),
    GERM_ONLINE("GermOnline"), GLY_CONNECT("GlyConnect"), GUIDETO_PHARMOCOLOGY("GuidetoPHARMACOLOGY"),
    HAMAP, HAMAP_RULE("HAMAP-Rule"), HGNC, H_INV_DB("H-InvDB"), HOGENOM, HOVERGEN, HPA, HPRD, HSSP,
    IFO, IMGT_GENE_DB("IMGT_GENE-DB"), IN_PARANOID("InParanoid"), INT_ACT("IntAct"), INTER_PRO("InterPro"), I_PTM_NET("iPTMnet"),
    JCRB,
    KEGG, KO,
    LOC,
    MALA_CARDS("MalaCards"), MAX_QB("MaxQB"), MESH("MeSH"), MEROPS, MGI, MIM, MINT, MOON_DB("MoonDB"), MOON_PROT("MoonProt"),
    NEXT_BIO("NextBio"), NEXTPROT_SUBMISSION("neXtProtSubmission"), NIH_ARP("NIH-ARP"),
    OBO, OGP, OMA, OPEN_TARGETS("OpenTargets"), ORPHANET("Orphanet"), ORTHO_DB("OrthoDB"),
    PANTHER, PAX_DB("PaxDb"), PDB, PDB_SUM("PDBsum"), PEPTIDE_ATLAS("PeptideAtlas"), PEROXIBASE("PeroxiBase"),
    PFAM("Pfam"), PHARM_GKB("PharmGKB"), PHOSPHO_SITE_PLUS("PhosphoSitePlus"), PHYLOM_DB("PhylomeDB"), PIR, PIRNR,
    PIRSF, PMAP_CUT_DB("PMAP-CutDB"), PRIDE, PRINTS, PRO, PRO_DOM("ProDom"), PROSITE, PROSITE_PRORULE("PROSITE-ProRule"),
    PROTEIN_MODEL_PORTAL("ProteinModelPortal"), PROTEOMES("Proteomes"), PROTEOMICS_DB("ProteomicsDB"), PROTEOPEDIA, PUB_MED("PubMed"),
    REBASE, REF_SEQ("RefSeq"), REPRODUCTION_2DPAGE("REPRODUCTION-2DPAGE"), RULEBASE("RuleBase"),
    SABIO_RK("SABIO-RK"), SFLD, SIGNA_LINK("SignaLink"), SIGNOR, SMART, SMR, SRM_ATLAS("SRMAtlas"), STRING,
    SUPFAM, SWISS_2DPAGE("SWISS-2DPAGE"), SWISS_LIPIDS("SwissLipids"), SWISS_PALM("SwissPalm"),
    TIGRFAMS("TIGRFAMs"), TKG, TOP_DOWN_PROTEOMICS("TopDownProteomics"), TREE_FAM("TreeFam"),
    UCSC, UCD_2DPAGE("UCD-2DPAGE"), UNILECTIN("UniLectin"), UNIPATHWAY("UniPathway"), UNI_GENE("UniGene"), UNI_CARB_KB("UniCarbKB"), 
    UNIPROT("UniProt"), UNIPROT_CONTROL_VOC("UniProt control vocabulary"), UNIPROT_DOMAIN("UniProt domain"),
    PSIMOD("PSI-MOD"),
    WEBINFO
    ;

    private static EnumConstantDictionary<XrefDatabase> dictionaryOfConstants =
            new EnumConstantDictionary<XrefDatabase>(XrefDatabase.class, values()) {
                @Override
                protected void updateDictionaryOfConstants(Map<String, XrefDatabase> dictionary) {

                    for (XrefDatabase db : values()) {
                        dictionary.put(db.getName(), db);
                    }
                }
            };

    private final String name;

    XrefDatabase() {

	    this.name = name();
    }

    XrefDatabase(String name) {

        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static XrefDatabase valueOfName(String name) {

        return dictionaryOfConstants.valueOfKey(name);
    }

    @Override
    public EnumConstantDictionary<XrefDatabase> getEnumConstantDictionary() {

        return dictionaryOfConstants;
    }
}