package org.nextprot.api.core.utils.dbxref.conv;

import org.nextprot.api.core.domain.DbXref;

import java.util.List;

/**
 * Convert DbXrefProperty to DbXrefs
 *
 * Created by fnikitin on 22/12/15.
 */
public interface DbXrefPropertyToXrefConverter {

    /**
     * Convert DbXrefProperty get from xref into xrefs
     *
     * @param xref DbXrefProperty container
     * @return a list of DbXrefs
     */
    List<DbXref> convert(DbXref xref);
}
