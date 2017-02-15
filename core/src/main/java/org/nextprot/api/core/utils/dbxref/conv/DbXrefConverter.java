package org.nextprot.api.core.utils.dbxref.conv;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.utils.dbxref.resolver.XRefDatabase;

import java.util.*;

public class DbXrefConverter implements DbXrefPropertyToXrefConverter {

    private final Map<XRefDatabase, DbXrefPropertyToXrefConverter> converters;

    private DbXrefConverter() {

        converters = new EnumMap<>(XRefDatabase.class);
        converters.put(XRefDatabase.REF_SEQ, new RefSeqDbXrefConverter());
        converters.put(XRefDatabase.EMBL, new EmblDbXrefConverter());
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

        Optional<XRefDatabase> db = XRefDatabase.optionalValueOfDbName(xref.getDatabaseName());

        if (db.isPresent() && converters.containsKey(db.get())) {
            return converters.get(db.get()).convert(xref);
        }

        return new ArrayList<>();
    }
}
