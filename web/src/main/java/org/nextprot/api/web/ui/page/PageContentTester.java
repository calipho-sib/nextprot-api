package org.nextprot.api.web.ui.page;

import org.nextprot.api.core.domain.Entry;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Test entry content required to display by the page(s)
 */
public class PageContentTester {

    private static final Set<SimplePageConfig> ALL_PAGES;

    private final Entry entry;
    private final Set<SimplePageConfig> pages;

    static {
        ALL_PAGES = new HashSet<>();
        ALL_PAGES.addAll(Arrays.asList(
            ExonsPageConfig.getInstance(),
            ExpressionPageConfig.getInstance(),
            FunctionPageConfig.getInstance(),
            GeneIdentifiersPageConfig.getInstance(),
            IdentifiersPageConfig.getInstance(),
            InteractionsPageConfig.getInstance(),
            LocalisationPageConfig.getInstance(),
            MedicalPageConfig.getInstance(),
            PeptidesPageConfig.getInstance(),
            PhenotypesPageConfig.getInstance(),
            ProteomicsPageConfig.getInstance(),
            SequencePageConfig.getInstance(),
            StructuresPageConfig.getInstance()
        ));
    }

    PageContentTester(Entry entry) {

        Objects.requireNonNull(entry);

        this.entry = entry;
        pages = new HashSet<>();
    }

    public static PageContentTester allPages(Entry entry) {

        PageContentTester pageContentTester = new PageContentTester(entry);

        ALL_PAGES.forEach(pageContentTester::addPage);

        return pageContentTester;
    }

    void addPage(SimplePageConfig page) {

        Objects.requireNonNull(page);

        pages.add(page);
    }

    /**
     * @return the set of all page names testing entry content
     */
    public static Set<String> getAllTestingPageNames() {

        return ALL_PAGES.stream().map(SimplePageConfig::getPageName).collect(Collectors.toSet());
    }

    /**
     * Test entry pages and report the results in a map of boolean
     * @return for each true if entry provide data needed by the page else false
     */
    public Map<String, Boolean> testPageContent() {

        Map<String, Boolean> map = new HashMap<>(pages.size());

        for (SimplePageConfig page : pages) {

            if (page.hasContent(entry)) {
                map.put(page.getPageName(), Boolean.TRUE);
            }
            else {
                map.put(page.getPageName(), Boolean.FALSE);
            }
        }

        return map;
    }
}
