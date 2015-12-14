package org.nextprot.api.core.utils.dbxref;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.XRefDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * This singleton resolves DbXref url by delegating to DbXrefURLBaseResolver implementations
 */
public class DbXrefURLResolver {

    private static final DbXrefURLResolver INSTANCE = new DbXrefURLResolver();

    private final Map<XRefDatabase, DbXrefURLBaseResolver> resolvers;

    private DbXrefURLResolver() {

        resolvers = new HashMap<>();
        resolvers.put(XRefDatabase.WEBINFO, new WebInfoXrefURLResolver());
        resolvers.put(XRefDatabase.COSMIC, new CosmicXrefURLResolver());
        resolvers.put(XRefDatabase.EMBL, new EmblXrefURLResolver());
        resolvers.put(XRefDatabase.ENSEMBL, new EnsemblXrefURLResolver());
        resolvers.put(XRefDatabase.PIR, new PirXrefURLResolver());
    }

    public static DbXrefURLResolver getInstance() {
        return INSTANCE;
    }

    public String resolveUrl(DbXref xref) {

        Preconditions.checkNotNull(xref);

        XRefDatabase db = XRefDatabase.valueOfDbName(xref.getDatabaseName());

        if (resolvers.containsKey(db)) {
            return resolvers.get(db).resolve(xref);
        }

        throw new UnresolvedXrefURLException("xref id "+xref.getAccession()+": no resolver found (db: "+xref.getDatabaseName()+")");
    }
}
