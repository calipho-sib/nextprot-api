package org.nextprot.api.web.ui;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.web.ui.page.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Test entry content required to display by the page(s)
 */
public class PageDisplayTester {

    private static final Set<PageDisplayRequirement> ALL_PAGES;

    private final Entry entry;
    private final Set<PageDisplayRequirement> pages;

    static {
        ALL_PAGES = new HashSet<>();
        ALL_PAGES.addAll(Arrays.asList(
            ExonsPageDisplayRequirement.getInstance(),
            ExpressionPageDisplayRequirement.getInstance(),
            FunctionPageDisplayRequirement.getInstance(),
            GeneIdentifiersPageDisplayRequirement.getInstance(),
            IdentifiersPageDisplayRequirement.getInstance(),
            InteractionsPageDisplayRequirement.getInstance(),
            LocalisationPageDisplayRequirement.getInstance(),
            MedicalPageDisplayRequirement.getInstance(),
            PeptidesPageDisplayRequirement.getInstance(),
            PhenotypesPageDisplayRequirement.getInstance(),
            ProteomicsPageDisplayRequirement.getInstance(),
            SequencePageDisplayRequirement.getInstance(),
            StructuresPageDisplayRequirement.getInstance()
        ));
    }

    PageDisplayTester(Entry entry) {

        Objects.requireNonNull(entry);

        this.entry = entry;
        pages = new HashSet<>();
    }

    public static PageDisplayTester allPageRequirements(Entry entry) {

        PageDisplayTester pageDisplayTester = new PageDisplayTester(entry);

        ALL_PAGES.forEach(pageDisplayTester::addPageRequirement);

        return pageDisplayTester;
    }

    void addPageRequirement(PageDisplayRequirement page) {

        Objects.requireNonNull(page);

        if (pages.contains(page)) {
            throw new IllegalStateException("page requirement "+page.getPage().getLabel()+" already exists");
        }

        pages.add(page);
    }

    /**
     * @return the set of all page names testing entry content
     */
    public static Set<String> getAllTestingPageNames() {

        return ALL_PAGES.stream().map(PageDisplayRequirement::getPage).map(EntryPage::getLabel).collect(Collectors.toSet());
    }

    /**
     * Test entry pages and report the results in a map of boolean
     * @return for each true if entry provide data needed by the page else false
     */
    public Map<String, Boolean> testPageContent() {

        Map<String, Boolean> map = new HashMap<>(pages.size());

        for (PageDisplayRequirement page : pages) {

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
