package org.nextprot.api.core.utils.dbxref;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.XRefDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * This singleton resolves DbXref url by delegating to DbXrefURLBaseResolver implementations.
 *
 * It is backed by a map that associate a XRefDatabase to an instance of DbXrefURLBaseResolver.
 *
 * Each implementations of DbXrefURLBaseResolver are stateless as method resolve(url) can be invoked concurrently.
 */
public class DbXrefURLResolver {

    private final Map<XRefDatabase, DbXrefURLBaseResolver> resolvers;
    private final DbXrefURLBaseResolver oboResolver;

    private DbXrefURLResolver() {

        resolvers = new HashMap<>();
        resolvers.put(XRefDatabase.WEBINFO,        new WebInfoXrefURLResolver());
        resolvers.put(XRefDatabase.COSMIC,         new CosmicXrefURLResolver());
        resolvers.put(XRefDatabase.EMBL,           new EmblXrefURLResolver());
        resolvers.put(XRefDatabase.ENSEMBL,        new EnsemblXrefURLResolver());
        resolvers.put(XRefDatabase.PIR,            new PirXrefURLResolver());
        resolvers.put(XRefDatabase.CLINVAR,        new ClinvarXrefURLResolver());
        resolvers.put(XRefDatabase.GERMONLINE,     new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.GERMONLINE));
        resolvers.put(XRefDatabase.GENEVESTIGATOR, new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.GENEVESTIGATOR));
        resolvers.put(XRefDatabase.PROSITE,        new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.PROSITE));
        resolvers.put(XRefDatabase.PDB,            new ConstantLinkXrefURLResolver(CvDatabasePreferredLink.PDB));
        resolvers.put(XRefDatabase.HPA,            new HpaXrefURLResolver());
        resolvers.put(XRefDatabase.GENEVISIBLE,    new GenevisibleXrefURLResolver());
        resolvers.put(XRefDatabase.UNI_GENE,       new UnigeneXrefURLResolver());
        resolvers.put(XRefDatabase.UCSC,           new UcscXrefURLResolver());
        resolvers.put(XRefDatabase.INTACT,         new IntactXrefURLResolver());
        resolvers.put(XRefDatabase.HSSP,           new HsspXrefURLResolver());
        resolvers.put(XRefDatabase.BGEE,           new BgeeXrefURLResolver());
        resolvers.put(XRefDatabase.PEPTIDE_ATLAS,  new PeptideAtlasXrefURLResolver());
        resolvers.put(XRefDatabase.SRM_ATLAS,      new SrmAtlasXrefURLResolver());
        resolvers.put(XRefDatabase.TKG,            new TkgXrefURLResolver());
        resolvers.put(XRefDatabase.NIH_ARP,        new NihArpXrefURLResolver());
        resolvers.put(XRefDatabase.CGH_DB,         new CghDbArpXrefURLResolver());
        resolvers.put(XRefDatabase.IFO,            new LowerCaseAccessionXrefURLResolver());
        resolvers.put(XRefDatabase.JCRB,           new LowerCaseAccessionXrefURLResolver());

        oboResolver = new OboLibraryXrefURLResolver();
    }

    public static DbXrefURLResolver getInstance() {
        return Loader.INSTANCE;
    }

    /**
     * Does a thread-safe lazy-initialization of the instance without explicit synchronization
     * @see <a href="http://stackoverflow.com/questions/11165852/java-singleton-and-synchronization">java-singleton-and-synchronization</a>
     */
    private static class Loader {

        private static DbXrefURLResolver INSTANCE = new DbXrefURLResolver();
    }

    /**
     * Resolve xref linked url
     *
     * @param xref the xref containing linked url to resolved
     * @return a resolved url
     * @throws UnresolvedXrefURLException if url cannot be resolved
     */
    public String resolve(DbXref xref) {

        Preconditions.checkNotNull(xref);

        Optional<XRefDatabase> db = XRefDatabase.optionalValueOfDbName(xref.getDatabaseName());

        if (db.isPresent() && resolvers.containsKey(db.get())) {
            return resolvers.get(db.get()).resolve(xref);
        }
        else if (xref.getLinkUrl().contains("purl.obolibrary.org/obo")) {
            return oboResolver.resolve(xref);
        }

        throw new UnresolvedXrefURLException("xref id "+xref.getAccession()+": no resolver found for unknown linked db "+xref.getDatabaseName());
    }
}
