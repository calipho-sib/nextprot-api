package org.nextprot.api.core.utils.dbxref;

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
     * Resolve resolve(xref) and replace %u with given accession
     *
     * @param xref the xref containing linked url to resolved
     * @param accession the accession to replace %u from template
     * @return resolved url
     */
    String resolveWithAccession(DbXref xref, String accession);

    /**
     * @return the URL template of the given xref
     */
    String getTemplateURL(DbXref xref);
}
