package org.nextprot.api.core.utils.dbxref.resolver;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.utils.dbxref.DbXrefURLResolver;

import java.util.Optional;


/**
 * Main entry point object resolves DbXref url by delegating to DbXrefURLBaseResolver implementations.
 */
public class DbXrefURLResolverDelegate implements DbXrefURLResolver {

    private Optional<DbXrefURLResolverSupplier> getXRefResolverSupplier(DbXref xref) {

        Preconditions.checkNotNull(xref, "cannot resolve undefined DbXref");

        return DbXrefURLResolverSupplier.fromDbName(xref.getDatabaseName());
    }

    @Override
    public String resolve(DbXref xref) {

        Optional<DbXrefURLResolverSupplier> optionalSupplier = getXRefResolverSupplier(xref);

        if (optionalSupplier.isPresent()) {
            return optionalSupplier.get().getResolver().resolve(xref);
        }
        else if (xref.getLinkUrl().contains("purl.obolibrary.org/obo")) {
            return DbXrefURLResolverSupplier.OBO.getResolver().resolve(xref);
        }
        return new DefaultDbXrefURLResolver().resolve(xref);
    }

    @Override
    public String getTemplateURL(DbXref xref) {

        return getXRefResolverSupplier(xref)
                .map(d -> d.getResolver().getTemplateURL(xref))
                .orElse("");
    }

    @Override
    public String getValidXrefURL(String xrefURL, String databaseName) {

        return DbXrefURLResolverSupplier.fromDbName(databaseName)
                .map(d -> d.getResolver().getValidXrefURL(xrefURL, databaseName))
                .orElse(xrefURL);
    }
}
