package org.nextprot.api.core.utils.dbxref.conv;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.utils.dbxref.resolver.DbXrefURLResolverSupplier;

import java.util.*;

public class DbXrefConverter implements DbXrefPropertyToXrefConverter {

    private final Map<DbXrefURLResolverSupplier, DbXrefPropertyToXrefConverter> converters;

    private DbXrefConverter() {

        converters = new EnumMap<>(DbXrefURLResolverSupplier.class);
        converters.put(DbXrefURLResolverSupplier.REF_SEQ, new RefSeqDbXrefConverter());
        converters.put(DbXrefURLResolverSupplier.EMBL, new EmblDbXrefConverter());
    }

    public static DbXrefConverter getInstance() {
        return Loader.INSTANCE;
    }

    /**
     * Does a thread-safe lazy-initialization of the instance without explicit synchronization
     * @see <a href="http://stackoverflow.com/questions/11165852/java-singleton-and-synchronization">java-singleton-and-synchronization</a>
     */
    private static class Loader {

        private static DbXrefConverter INSTANCE = new DbXrefConverter();

        private Loader() {
            throw new IllegalAccessError("Cannot instanciate");
        }
    }

    @Override
    public List<DbXref> convert(DbXref xref) {

        Preconditions.checkNotNull(xref);

        Optional<DbXrefURLResolverSupplier> optResolverSupplier = DbXrefURLResolverSupplier.fromExistingDbName(xref.getDatabaseName());

        if (optResolverSupplier.isPresent() && converters.containsKey(optResolverSupplier.get())) {
            return converters.get(optResolverSupplier.get()).convert(xref);
        }

        return new ArrayList<>();
    }
}
