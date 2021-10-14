package org.nextprot.api.core.service.dbxref.resolver;

import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.service.dbxref.DbXrefURLResolver;
import org.nextprot.api.core.service.dbxref.XrefDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Supply a DbXrefURLResolver from a specific database
 *
 * Created by fnikitin on 10/12/15.
 */
public enum DbXrefURLResolverSupplier {

    BGEE(XrefDatabase.BGEE,                         new BgeeXrefURLResolver()),
    BRENDA(XrefDatabase.BRENDA,                     new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.BRENDA)),
    CELLOSAURUS(XrefDatabase.CELLOSAURUS,           new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.CELLOSAURUS)),
    CGH_DB(XrefDatabase.CGH_DB,                     new CghDbArpXrefURLResolver()),
    CHITARS(XrefDatabase.CHITARS,                   new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.CHITARS)),
    CLINVAR(XrefDatabase.CLINVAR,                   new ClinvarXrefURLResolver()),
    COSMIC(XrefDatabase.COSMIC,                     new CosmicXrefURLResolver()),
    DECIPHER(XrefDatabase.DECIPHER,                 new DecipherXrefURLResolver()),
    ECO(XrefDatabase.ECO,                         	new EcoXrefURLResolver()),
    EMBL(XrefDatabase.EMBL,                         new EmblXrefURLResolver()),
    EMBL_CDS(XrefDatabase.EMBL_CDS,                 new EmblCdsXrefURLResolver()),
    ENSEMBL(XrefDatabase.ENSEMBL,                   new EnsemblXrefURLResolver()),
    EVOC(XrefDatabase.EVOC,				 			new UnresolvedXrefURLResolver()),
    EXPRESSION_ATLAS(XrefDatabase.EXPRESSION_ATLAS, new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.EXPRESSION_ATLAS)),
    GENEVESTIGATOR(XrefDatabase.GENEVESTIGATOR,     new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.GENEVESTIGATOR)),
    GENEVISIBLE(XrefDatabase.GENEVISIBLE,           new GenevisibleXrefURLResolver()),
    GERMONLINE(XrefDatabase.GERM_ONLINE,            new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.GERMONLINE)),
    GLY_CONNECT(XrefDatabase.GLY_CONNECT,           new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.GLY_CONNECT)),
    HAMAP_RULE(XrefDatabase.HAMAP_RULE,             new ConstantXrefDatabasebURLResolver("http://hamap.expasy.org/")),
    HPA(XrefDatabase.HPA,                           new HpaXrefURLResolver()), 
    HSSP(XrefDatabase.HSSP,                         new HsspXrefURLResolver()),
    IFO(XrefDatabase.IFO,                           new JcrbXrefURLResolver()),
    INTACT(XrefDatabase.INT_ACT,                    new IntactXrefURLResolver()),
    JCRB(XrefDatabase.JCRB,                         new JcrbXrefURLResolver()),
    LOC(XrefDatabase.LOC,                 		    new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.LOC)),
    MESH(XrefDatabase.MESH,                         new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.MESH)),
    MASSIVE(XrefDatabase.MASSIVE, 					new MassiveXrefURLResolver() ),
    NIH_ARP(XrefDatabase.NIH_ARP,                   new NihArpXrefURLResolver()),
    OBO(XrefDatabase.OBO,                           new OboLibraryXrefURLResolver()),
    PDB(XrefDatabase.PDB,                           new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.PDB)),
    PEPTIDE_ATLAS(XrefDatabase.PEPTIDE_ATLAS,       new PeptideAtlasXrefURLResolver()),
    PEROXIBASE(XrefDatabase.PEROXIBASE,             new PeroxiBaseXrefURLResolver()),
    PIR(XrefDatabase.PIR,                           new PirXrefURLResolver()),
    PROSITE(XrefDatabase.PROSITE,                   new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.PROSITE)),
    PROSITE_PRORULE(XrefDatabase.PROSITE_PRORULE,   new ConstantXrefDatabasebURLResolver("http://prosite.expasy.org/")),
    PROTEOPEDIA(XrefDatabase.PROTEOPEDIA,           new DefaultDbXrefURLResolver()),
    PSIMOD(XrefDatabase.PSIMOD, 					new PsiModXrefURLResolver()),
    REF_SEQ(XrefDatabase.REF_SEQ,                   new DefaultDbXrefURLResolver()),
    RNACT(XrefDatabase.RNACT,                     	new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.RNACT)),
    RULEBASE(XrefDatabase.RULEBASE,                 new RuleBaseXrefURLResolver()),
    SIGNOR(XrefDatabase.SIGNOR,                     new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.SIGNOR)),
    SMR(XrefDatabase.SMR,                           new SmrXrefURLResolver()),
    SRM_ATLAS(XrefDatabase.SRM_ATLAS,               new SrmAtlasXrefURLResolver()),
    TKG(XrefDatabase.TKG,                           new TkgXrefURLResolver()),
    UCSC(XrefDatabase.UCSC,                         new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.UCSC)),
    UNIPATHWAY(XrefDatabase.UNIPATHWAY,             new UnresolvedXrefURLResolver()),
    UNI_GENE(XrefDatabase.UNI_GENE,                 new UnigeneXrefURLResolver()),
    UNIPROT_CV(XrefDatabase.UNIPROT_CONTROL_VOC,    new UniprotControlVocabularyXrefURLResolver()),
    UNIPROT_DOMAIN(XrefDatabase.UNIPROT_DOMAIN,     new UniprotDomainXrefURLResolver()),
    VARIO(XrefDatabase.VARIO     ,                  new VariOXrefURLResolver()),
    WEBINFO(XrefDatabase.WEBINFO,                   new WebInfoXrefURLResolver())
    ;

    private final XrefDatabase xrefDatabase;
    private final DefaultDbXrefURLResolver resolver;

    private static final Map<String, DbXrefURLResolverSupplier> VALUE_OF_STRING_MAP = new HashMap<>();

    static {
        for (DbXrefURLResolverSupplier dbXrefURLResolverSupplier : values()) {

            VALUE_OF_STRING_MAP.put(dbXrefURLResolverSupplier.getXrefDatabase().getName().toUpperCase(), dbXrefURLResolverSupplier);
        }
    }

    DbXrefURLResolverSupplier(XrefDatabase xrefDatabase, DefaultDbXrefURLResolver resolver) {

        this.xrefDatabase = xrefDatabase;
        this.resolver = resolver;
    }

    public XrefDatabase getXrefDatabase() {
        return xrefDatabase;
    }

    public DbXrefURLResolver getResolver() {

        return resolver;
    }

    /**
     * Get a resolver supplier from a database name
     * @param dbName the database name
     * @return an Optional of DbXrefURLResolverSupplier enum or absent if not found
     */
    public static Optional<DbXrefURLResolverSupplier> fromDbName(String dbName) {

        return Optional.ofNullable(VALUE_OF_STRING_MAP.get(dbName.toUpperCase()));
    }
}
