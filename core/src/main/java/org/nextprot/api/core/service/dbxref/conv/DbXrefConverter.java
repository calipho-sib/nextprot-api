package org.nextprot.api.core.service.dbxref.conv;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.service.dbxref.XrefDatabase;
import org.nextprot.api.core.service.dbxref.resolver.DbXrefURLResolverSupplier;

import java.util.*;

public class DbXrefConverter implements DbXrefPropertyToXrefConverter {

    private final Map<XrefDatabase, DbXrefPropertyToXrefConverter> converters;

    private DbXrefConverter() {

        converters = new EnumMap<>(XrefDatabase.class);
        converters.put(XrefDatabase.REF_SEQ, new RefSeqDbXrefConverter());
        converters.put(XrefDatabase.EMBL, new EmblDbXrefConverter());
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
            throw new IllegalAccessError("Cannot instanciate Loader");
        }
    }

    @Override
    public List<DbXref> convert(DbXref xref) {

        Preconditions.checkNotNull(xref);

        Optional<DbXrefURLResolverSupplier> optionalSupplier = DbXrefURLResolverSupplier.fromDbName(xref.getDatabaseName());

        if (optionalSupplier.isPresent() && converters.containsKey(optionalSupplier.get().getXrefDatabase())) {
            return converters.get(optionalSupplier.get().getXrefDatabase()).convert(xref);
        }

        return new ArrayList<>();
    }
}
