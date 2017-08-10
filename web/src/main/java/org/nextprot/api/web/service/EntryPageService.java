package org.nextprot.api.web.service;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.annotation.ValidEntry;

import java.util.Map;

public interface EntryPageService {

    /**
     * Test all display requirements for all registered entry pages and report the results in a map of boolean
     * @param entryName the nextprot accession number
     * @return for each true if entry provide data needed by the page else false
     */
    Map<String, Boolean> testEntryContentForPageDisplay(@ValidEntry String entryName);

    /**
     * Keep data from Entry specific to the page view
     * @param entryName the nextprot accession number
     * @param pageViewName the page view name (ex: "sequence", "function", ...)
     * @return a slimmer Entry
     */
    Entry filterEntryContentInPageView(String entryName, String pageViewName);
}
