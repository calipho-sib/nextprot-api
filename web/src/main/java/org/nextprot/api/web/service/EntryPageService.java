package org.nextprot.api.web.service;

import org.nextprot.api.core.service.annotation.ValidEntry;

import java.util.Map;
import java.util.Set;

public interface EntryPageService {

    /**
     * Get the set of all pages that need to test for entry content
     * @return the set of all page names
     */
    Set<String> getAllTestingPageNames();

    /**
     * Test entry content for page display
     * @param entryName the nextprot accession number
     * @return a map of page name to boolean
     */
    Map<String, Boolean> testEntryContentForPageDisplay(@ValidEntry String entryName);
}
