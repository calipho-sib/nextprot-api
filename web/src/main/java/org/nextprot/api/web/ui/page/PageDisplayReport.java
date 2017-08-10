package org.nextprot.api.web.ui.page;

import org.nextprot.api.core.domain.Entry;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Reports results of entry page tests defined by {@code PageDisplayPredicate}s
 */
public class PageDisplayReport {

    /**
     * Unique entry point to build instance of this class
     * @return an instance of tester
     */
    public static PageDisplayReport allPages() {

        return new PageDisplayReport();
    }

    /**
     * Test all display requirements for all registered entry pages and report the results in a map of boolean
     * @return for each true if entry provide data needed by the page else false
     */
    public Map<String, Boolean> reportDisplayPageStatus(Entry entry) {

        Objects.requireNonNull(entry);

        Map<String, Boolean> map = new HashMap<>();

        for (EntryPage page : EntryPage.values()) {

            map.put(page.getLabel(), page.buildPageView().doDisplayPage(entry));
        }

        return map;
    }
}
