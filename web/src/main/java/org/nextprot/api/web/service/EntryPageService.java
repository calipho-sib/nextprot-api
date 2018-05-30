package org.nextprot.api.web.service;

import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.service.annotation.ValidEntry;

import java.util.List;
import java.util.Map;

public interface EntryPageService {

    /**
     * Test all display requirements for all registered entry pages and report the results in a map of boolean
     * @param entryName the nextprot accession number
     * @return for each true if entry provide data needed by the page else false
     */
    Map<String, Boolean> hasContentForPageDisplay(@ValidEntry String entryName);

    /**
     * Extract xrefs specific to the page view
     * @param entryName the nextprot accession number
     * @param pageViewName the page view name (ex: "sequence", "function", ...)
     * @return the list of xrefs for the page view
     */
    List<DbXref> extractXrefForPageView(String entryName, String pageViewName);
}
