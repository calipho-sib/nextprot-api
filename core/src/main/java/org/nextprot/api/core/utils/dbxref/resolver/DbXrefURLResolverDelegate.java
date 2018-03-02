package org.nextprot.api.core.utils.dbxref.resolver;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.utils.dbxref.DbXrefURLResolver;

import java.util.Optional;


/**
 * Main entry point object resolves DbXref url by delegating to DbXrefURLBaseResolver implementations.
 */
public class DbXrefURLResolverDelegate implements DbXrefURLResolver {

    private Optional<DbXrefURLResolverSupplier> getXRefDatabase(DbXref xref) {

        Preconditions.checkNotNull(xref, "cannot resolve undefined DbXref");

        return DbXrefURLResolverSupplier.fromExistingDbName(xref.getDatabaseName());
    }

    @Override
    public String resolve(DbXref xref) {

        Optional<DbXrefURLResolverSupplier> db = getXRefDatabase(xref);

        if (db.isPresent()) {
            return db.get().get().resolve(xref);
        }
        else if (xref.getLinkUrl().contains("purl.obolibrary.org/obo")) {
            return DbXrefURLResolverSupplier.OBO.get().resolve(xref);
        }
        return new DefaultDbXrefURLResolver().resolve(xref);
    }

    @Override
    public String getTemplateURL(DbXref xref) {

        return getXRefDatabase(xref)
                .map(d -> d.get().getTemplateURL(xref))
                .orElseGet(() -> "");
    }

    @Override
    public String getValidXrefURL(String xrefURL, String databaseName) {

        return DbXrefURLResolverSupplier.fromExistingDbName(databaseName)
                .map(d -> d.get().getValidXrefURL(xrefURL, databaseName))
                .orElseGet(() -> xrefURL);
    }
}
