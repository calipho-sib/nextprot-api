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

    private final Map<EntryPage, PageView> registeredPredicates;

    PageDisplayReport() {
        registeredPredicates = new EnumMap<>(EntryPage.class);
    }

    /**
     * Unique entry point to build instance of this class
     * @return an instance of tester
     */
    public static PageDisplayReport allPages() {

        PageDisplayReport pageDisplayReport = new PageDisplayReport();

        PageViewBase.Predicates.getInstance().getPagePredicates()
                .forEach(pageDisplayReport::addPredicate);

        return pageDisplayReport;
    }

    /**
     * Add a page display requirement
     * @param pageDisplayPredicate a requirement to test page display
     */
    void addPredicate(PageView pageDisplayPredicate) {

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
    public Map<String, Boolean> reportDisplayPageStatus(Entry entry) {

        Objects.requireNonNull(entry);

        Map<String, Boolean> map = new HashMap<>(registeredPredicates.size());

        for (PageView page : registeredPredicates.values()) {

            map.put(page.getPage().getLabel(), page.doDisplayPage(entry));
        }

        return map;
    }
}
