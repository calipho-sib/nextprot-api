package org.nextprot.api.core.utils.dbxref.resolver;

import org.nextprot.api.core.domain.CvDatabasePreferredLink;

import java.util.Optional;

/**
 * Databases referenced in DbXref with resolver
 *
 * Created by fnikitin on 10/12/15.
 */
public enum XRefDatabase {

    BGEE("Bgee",                     new BgeeXrefURLResolver()),
    BRENDA("Brenda",                 new DefaultDbXrefURLResolver()),
    CGH_DB("CGH-DB",                 new CghDbArpXrefURLResolver()),
    CLINVAR("Clinvar",               new ClinvarXrefURLResolver()),
    COSMIC("Cosmic",                 new CosmicXrefURLResolver()),
    EMBL("EMBL",                     new EmblXrefURLResolver()),
    ENSEMBL("Ensembl",               new EnsemblXrefURLResolver()),
    GENEVESTIGATOR("Genevestigator", new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.GENEVESTIGATOR)),
    GENEVISIBLE("Genevisible",       new GenevisibleXrefURLResolver()),
    GERMONLINE("GermOnline",         new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.GERMONLINE)),
    HPA("HPA",                       new HpaXrefURLResolver()),
    HSSP("HSSP",                     new HsspXrefURLResolver()),
    IFO("IFO",                       new JcrbXrefURLResolver()),
    INTACT("IntAct",                 new IntactXrefURLResolver()),
    JCRB("JCRB",                     new JcrbXrefURLResolver()),
    NIH_ARP("NIH-ARP",               new NihArpXrefURLResolver()),
    OBO("OBO",                       new OboLibraryXrefURLResolver()),
    PDB("PDB",                       new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.PDB)),
    PEPTIDE_ATLAS("PeptideAtlas",    new PeptideAtlasXrefURLResolver()),
    PEROXIBASE("PeroxiBase",         new PeroxiBaseXrefURLResolver()),
    PIR("PIR",                       new PirXrefURLResolver()),
    PROSITE("PROSITE",               new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.PROSITE)),
    PROTEOPEDIA("PROTEOPEDIA",       new DefaultDbXrefURLResolver()),
    REF_SEQ("RefSeq",                new DefaultDbXrefURLResolver()),
    SMR("SMR",                       new SMRXrefURLResolver()),
    SRM_ATLAS("SRMAtlas",            new SrmAtlasXrefURLResolver()),
    TKG("TKG",                       new TkgXrefURLResolver()),
    UCSC("UCSC",                     new UcscXrefURLResolver()),
    UNI_GENE("UniGene",              new UnigeneXrefURLResolver()),
    WEBINFO("WEBINFO",               new WebInfoXrefURLResolver()),
    ;

    private final String name;
    private final DefaultDbXrefURLResolver resolver;

    XRefDatabase(String name, DefaultDbXrefURLResolver resolver) {

        this.name = name;
        this.resolver = resolver;
    }

    public String getName() {
        return name;
    }

    public DefaultDbXrefURLResolver getResolver() {

        return resolver;
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
    public static Optional<XRefDatabase> valueOfName(String name) {

        return Optional.ofNullable(valueOfDbName(name));
    }
}
