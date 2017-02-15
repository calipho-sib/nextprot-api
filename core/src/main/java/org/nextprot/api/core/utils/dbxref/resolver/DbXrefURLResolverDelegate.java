package org.nextprot.api.core.utils.dbxref.resolver;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.utils.dbxref.DbXrefURLResolver;

import java.util.Optional;


/**
 * Main entry point object resolves DbXref url by delegating to DbXrefURLBaseResolver implementations.
 */
public class DbXrefURLResolverDelegate implements DbXrefURLResolver {

    private Optional<XRefDatabase> getXRefDatabase(DbXref xref) {

        Preconditions.checkNotNull(xref, "cannot resolve undefined DbXref");

        return XRefDatabase.valueOfName(xref.getDatabaseName());
    }

    @Override
    public String resolve(DbXref xref) {

        Optional<XRefDatabase> db = getXRefDatabase(xref);

        if (db.isPresent()) {
            return db.get().getResolver().resolve(xref);
        }
        else if (xref.getLinkUrl().contains("purl.obolibrary.org/obo")) {
            return XRefDatabase.OBO.getResolver().resolve(xref);
        }
        return new DefaultDbXrefURLResolver().resolve(xref);
    }

    @Override
    public String resolveWithAccession(DbXref xref, String accession) {

        return new DefaultDbXrefURLResolver().resolveWithAccession(xref, accession);
    }

    @Override
    public String getTemplateURL(DbXref xref) {

        return getXRefDatabase(xref)
                .map(d -> d.getResolver().getTemplateURL(xref))
                .orElseGet(() -> "");
    }
}
