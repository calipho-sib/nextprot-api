package org.nextprot.api.core.utils.dbxref.resolver;

import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.utils.dbxref.DbXrefURLResolver;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Supply a DbXrefURLResolver from a specific database
 *
 * Created by fnikitin on 10/12/15.
 */
public enum DbXrefURLResolverSupplier implements Supplier<DbXrefURLResolver> {

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
    EXPRESSION_ATLAS("ExpressionAtlas", new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.EXPRESSION_ATLAS)),
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
    CHITARS("ChiTaRS",                 new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.CHITARS)),  
    LOC("LOC",                 		   new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.LOC))
    ;

    private final String name;
    private final DefaultDbXrefURLResolver resolver;

    private static final Map<String, DbXrefURLResolverSupplier> VALUE_OF_STRING_MAP = new HashMap<>();

    static {
        for (DbXrefURLResolverSupplier dbXrefURLResolverSupplier : values()) {

            VALUE_OF_STRING_MAP.put(dbXrefURLResolverSupplier.getName().toUpperCase(), dbXrefURLResolverSupplier);
        }
    }

    DbXrefURLResolverSupplier(String name, DefaultDbXrefURLResolver resolver) {

        this.name = name;
        this.resolver = resolver;
    }

    public String getName() {
        return name;
    }

    @Override
    public DbXrefURLResolver get() {

        return resolver;
    }

    /**
     * Get a XRefDatabase enum from name
     * @param dbName the database name
     * @return XRefDatabase enum or null if not found
     */
    public static DbXrefURLResolverSupplier fromDbName(String dbName) {

        String dbNameUpperCase = dbName.toUpperCase();

        if (VALUE_OF_STRING_MAP.containsKey(dbNameUpperCase)) {
            return VALUE_OF_STRING_MAP.get(dbNameUpperCase);
        }
        return null;
    }

    /**
     * Get an optional enum from a database name
     * @param dbName the database name
     * @return an Optional of DbXrefURLResolverSupplier enum or absent if not found
     */
    public static Optional<DbXrefURLResolverSupplier> fromExistingDbName(String dbName) {

        return Optional.ofNullable(fromDbName(dbName));
    }
}
