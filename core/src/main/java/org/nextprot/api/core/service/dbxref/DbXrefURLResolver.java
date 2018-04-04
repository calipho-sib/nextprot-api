package org.nextprot.api.core.service.dbxref;

import org.nextprot.api.core.domain.DbXref;

/**
 * Resolve DbXref templates URL
 *
 * Created by fnikitin on 14.02.17.
 */
public interface DbXrefURLResolver {

    /**
     * Resolve xref linked url
     *
     * @param xref the xref containing linked url to resolved
     * @return a resolved url
     */
    String resolve(DbXref xref);

    /**
     * @return the URL template of the given xref
     */
    String getTemplateURL(DbXref xref);

    /**
     * Get valid Xref URL (when bad value from npdb)
     * @param xrefURL the xref URL
     * @param databaseName the xref database name
     * @return a valid xref URL
     */
    String getValidXrefURL(String xrefURL, String databaseName);
}
