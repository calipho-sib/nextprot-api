package org.nextprot.api.core.utils.dbxref.resolver;

import org.nextprot.api.core.domain.CvDatabasePreferredLink;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Databases referenced in DbXref with resolver
 *
 * Created by fnikitin on 10/12/15.
 */
public enum XRefDatabase {

    BGEE("Bgee",                       new BgeeXrefURLResolver()),
    BRENDA("BRENDA",                   new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.BRENDA)),
    CGH_DB("CGH-DB",                   new CghDbArpXrefURLResolver()),
    CLINVAR("Clinvar",                 new ClinvarXrefURLResolver()),
    COSMIC("Cosmic",                   new CosmicXrefURLResolver()),
    EMBL("EMBL",                       new EmblXrefURLResolver()),
    ENSEMBL("Ensembl",                 new EnsemblXrefURLResolver()),
    GENEVESTIGATOR("Genevestigator",   new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.GENEVESTIGATOR)),
    GENEVISIBLE("Genevisible",         new GenevisibleXrefURLResolver()),
    GERMONLINE("GermOnline",           new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.GERMONLINE)),
    HAMAP_RULE("HAMAP-Rule",           new ConstantXrefDatabasebURLResolver("http://hamap.expasy.org/")),
    HPA("HPA",                         new HpaXrefURLResolver()),
    HSSP("HSSP",                       new HsspXrefURLResolver()),
    IFO("IFO",                         new JcrbXrefURLResolver()),
    INTACT("IntAct",                   new IntactXrefURLResolver()),
    JCRB("JCRB",                       new JcrbXrefURLResolver()),
    NIH_ARP("NIH-ARP",                 new NihArpXrefURLResolver()),
    OBO("OBO",                         new OboLibraryXrefURLResolver()),
    PDB("PDB",                         new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.PDB)),
    PEPTIDE_ATLAS("PeptideAtlas",      new PeptideAtlasXrefURLResolver()),
    PEROXIBASE("PeroxiBase",           new PeroxiBaseXrefURLResolver()),
    PIR("PIR",                         new PirXrefURLResolver()),
    PROSITE("PROSITE",                 new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.PROSITE)),
    PROSITE_PRORULE("PROSITE-ProRule", new ConstantXrefDatabasebURLResolver("http://prosite.expasy.org/")),
    PROTEOPEDIA("PROTEOPEDIA",         new DefaultDbXrefURLResolver()),
    REF_SEQ("RefSeq",                  new DefaultDbXrefURLResolver()),
    RULEBASE("RuleBase",               new RuleBaseXrefURLResolver()),
    SIGNOR("SIGNOR",                   new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.SIGNOR)),
    SMR("SMR",                         new SmrXrefURLResolver()),
    SRM_ATLAS("SRMAtlas",              new SrmAtlasXrefURLResolver()),
    TKG("TKG",                         new TkgXrefURLResolver()),
    UCSC("UCSC",                       new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.UCSC)),
    UNI_GENE("UniGene",                new UnigeneXrefURLResolver()),
    WEBINFO("WEBINFO",                 new WebInfoXrefURLResolver()),
    ;

    private final String name;
    private final DefaultDbXrefURLResolver resolver;

    private static final Map<String, XRefDatabase> VALUE_OF_STRING_MAP = new HashMap<>();

    static {
        for (XRefDatabase xRefDatabase : values()) {

            VALUE_OF_STRING_MAP.put(xRefDatabase.getName().toUpperCase(), xRefDatabase);
        }
    }

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

        String uc = name.toUpperCase();

        if (VALUE_OF_STRING_MAP.containsKey(uc)) {
            return VALUE_OF_STRING_MAP.get(uc);
        }
        return null;
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
