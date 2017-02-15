package org.nextprot.api.core.utils.dbxref;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.utils.dbxref.resolver.UnresolvedXrefURLException;
import org.nextprot.api.core.utils.dbxref.resolver.XRefDatabase;

import java.util.Optional;


/**
 * This singleton resolves DbXref url by delegating to DbXrefURLBaseResolver implementations.
 *
 * It is backed by a map that associate a XRefDatabase to an instance of DbXrefURLBaseResolver.
 *
 * Each implementations of DbXrefURLBaseResolver are stateless as method resolve(url) can be invoked concurrently.
 */
public class DbXrefURLResolver {

    /**
     * Resolve xref linked url
     *
     * @param xref the xref containing linked url to resolved
     * @return a resolved url
     * @throws UnresolvedXrefURLException if url cannot be resolved
     */
    public String resolve(DbXref xref) {

        Preconditions.checkNotNull(xref, "cannot resolve undefined DbXref");

        Optional<XRefDatabase> db = XRefDatabase.optionalValueOfDbName(xref.getDatabaseName());

        if (db.isPresent()) {
            return db.get().getResolver().resolve(xref);
        }
        else if (xref.getLinkUrl().contains("purl.obolibrary.org/obo")) {
            return XRefDatabase.OBO.getResolver().resolve(xref);
        }
        else {
            return new DbXrefURLBaseResolver().resolve(xref);
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

        if (xref != null && xref.getLinkUrl() != null && xref.getLinkUrl().contains("%u")) {

            Optional<XRefDatabase> db = XRefDatabase.optionalValueOfDbName(xref.getDatabaseName());

            String templateURL = db.isPresent() ? db.get().getResolver().getTemplateURL(xref) : xref.getLinkUrl();

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

            String resolved = templateURL.replaceFirst("%u", accession.startsWith("NX_") ? accession.substring(3) : accession);

            return resolved.replaceFirst("%s", xref.getAccession());
        }

        return resolve(xref);
    }
}
