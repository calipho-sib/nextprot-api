package org.nextprot.api.core.utils.dbxref;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.XRefDatabase;

import java.util.EnumMap;
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
    private final DbXrefURLBaseResolver defaultResolver = new DbXrefURLBaseResolver();

    private DbXrefURLResolver() {

        resolvers = new EnumMap<>(XRefDatabase.class);
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
        resolvers.put(XRefDatabase.IFO,            new JcrbXrefURLResolver());
        resolvers.put(XRefDatabase.JCRB,           new JcrbXrefURLResolver());

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

        private Loader() {
            throw new IllegalAccessError("Non instanciable");
        }
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
        else {
            return defaultResolver.resolve(xref);
        }
    }

    /**
     * Call method resolve(xref) then continue resolution by replacing %u with accession
     *
     * @param xref the xref containing linked url to resolved
     * @param accession the accession to replace %u from template
     * @return resolved url
     * @throws UnresolvedXrefURLException if url cannot be resolved
     */
    // TODO: this implementation is ugly and should be refactored
    public String resolveWithAccession(DbXref xref, String accession) {

        if (xref.getLinkUrl().contains("%u")) {

            accession = accession.startsWith("NX_") ? accession.substring(3) : accession;
            String templateURL = xref.getLinkUrl();

            if (!templateURL.startsWith("http")) {
                templateURL = "http://" + templateURL;
            }

            if ("brenda".equalsIgnoreCase(xref.getDatabaseName())) {

                if (xref.getAccession().startsWith("BTO")) {
                    templateURL = CvDatabasePreferredLink.BRENDA_BTO.getLink().replace("%s", xref.getAccession().replace(":", "_"));
                } else {
                    templateURL = templateURL.replaceFirst("%s1", xref.getAccession());

                    // organism always human: hardcoded as "247"
                    templateURL = templateURL.replaceFirst("%s2", "247");
                }
            }

            String resolved = templateURL.replaceFirst("%u", accession);

            return resolved.replaceFirst("%s", xref.getAccession());
        }

        return resolve(xref);
    }
}
