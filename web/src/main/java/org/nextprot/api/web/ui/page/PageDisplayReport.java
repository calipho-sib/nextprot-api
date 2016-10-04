package org.nextprot.api.web.ui.page;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.web.ui.page.impl.*;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Reports results of entry page tests defined by {@code PageDisplayPredicate}s
 */
public class PageDisplayReport {

    private final Entry entry;
    private final Map<EntryPage, PageDisplayPredicate> registeredPredicates;

    PageDisplayReport(Entry entry) {

        Objects.requireNonNull(entry);

        this.entry = entry;
        registeredPredicates = new EnumMap<>(EntryPage.class);
    }

    /**
     * Unique entry point to build instance of this class
     * @param entry the entry to test for page display
     * @return an instance of tester
     */
    public static PageDisplayReport allPages(Entry entry) {

        PageDisplayReport pageDisplayReport = new PageDisplayReport(entry);

        PageDisplayBasePredicate.Predicates.getInstance().getPagePredicates()
                .forEach(pageDisplayReport::addPredicate);

        return pageDisplayReport;
    }

    /**
     * Add a page display requirement
     * @param pageDisplayPredicate a requirement to test page display
     */
    void addPredicate(PageDisplayPredicate pageDisplayPredicate) {

        Objects.requireNonNull(pageDisplayPredicate);

        if (registeredPredicates.containsKey(pageDisplayPredicate.getPage())) {
            throw new IllegalStateException("page requirement "+ pageDisplayPredicate.getPage().getLabel()+" already exists");
        }

        registeredPredicates.put(pageDisplayPredicate.getPage(), pageDisplayPredicate);
    }

    /**
     * Test all display requirements for all registered entry pages and report the results in a map of boolean
     * @return for each true if entry provide data needed by the page else false
     */
    public Map<String, Boolean> reportDisplayPageStatus() {

        Map<String, Boolean> map = new HashMap<>(registeredPredicates.size());

        for (PageDisplayPredicate page : registeredPredicates.values()) {

            if (page.doDisplayPage(entry)) {
                map.put(page.getPage().getLabel(), Boolean.TRUE);
            }
            else {
                map.put(page.getPage().getLabel(), Boolean.FALSE);
            }
        }

        return map;
    }
}
