package org.nextprot.api.web.service;

import org.nextprot.api.core.service.annotation.ValidEntry;

import java.util.Map;

public interface EntryPageService {

    /**
     * Test entry content for page display
     * @param entryName the nextprot accession number
     * @return a map of page name to boolean
     */
    Map<String, Boolean> testEntryContentForPageDisplay(@ValidEntry String entryName);
}
