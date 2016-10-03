package org.nextprot.api.web.ui.page;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.web.ui.page.impl.*;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Tests entry content required to display entry page(s) defined by {@code PageDisplayRequirement}s
 */
public class PageDisplayRequirementsTester {

    private final Entry entry;
    private final Map<EntryPage, PageDisplayRequirement> registeredPageDisplayRequirements;

    PageDisplayRequirementsTester(Entry entry) {

        Objects.requireNonNull(entry);

        this.entry = entry;
        registeredPageDisplayRequirements = new EnumMap<>(EntryPage.class);
    }

    /**
     * Unique entry point to build instance of this class
     * @param entry the entry to test for page display
     * @return an instance of tester
     */
    public static PageDisplayRequirementsTester allPageRequirements(Entry entry) {

        PageDisplayRequirementsTester pageDisplayRequirementsTester = new PageDisplayRequirementsTester(entry);

        BasePageDisplayRequirement.AllPageDisplayRequirements.getInstance().getPageRequirements()
                .forEach(pageDisplayRequirementsTester::addRequirement);

        return pageDisplayRequirementsTester;
    }

    /**
     * Add a page display requirement
     * @param pageDisplayRequirement a requirement to test page display
     */
    void addRequirement(PageDisplayRequirement pageDisplayRequirement) {

        Objects.requireNonNull(pageDisplayRequirement);

        if (registeredPageDisplayRequirements.containsKey(pageDisplayRequirement.getPage())) {
            throw new IllegalStateException("page requirement "+pageDisplayRequirement.getPage().getLabel()+" already exists");
        }

        registeredPageDisplayRequirements.put(pageDisplayRequirement.getPage(), pageDisplayRequirement);
    }

    /**
     * Test all display requirements for all registered entry pages and report the results in a map of boolean
     * @return for each true if entry provide data needed by the page else false
     */
    public Map<String, Boolean> doDisplayPages() {

        Map<String, Boolean> map = new HashMap<>(registeredPageDisplayRequirements.size());

        for (PageDisplayRequirement page : registeredPageDisplayRequirements.values()) {

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
