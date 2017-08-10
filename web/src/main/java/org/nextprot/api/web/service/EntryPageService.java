package org.nextprot.api.web.service;

import org.nextprot.api.core.service.annotation.ValidEntry;

import java.util.Map;

public interface EntryPageService {

    /**
     * Test all display requirements for all registered entry pages and report the results in a map of boolean
     * @param entryName the nextprot accession number
     * @return for each true if entry provide data needed by the page else false
     */
    Map<String, Boolean> testEntryContentForPageDisplay(@ValidEntry String entryName);
}
