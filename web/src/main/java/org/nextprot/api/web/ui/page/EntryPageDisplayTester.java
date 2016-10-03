package org.nextprot.api.web.ui.page;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.web.ui.page.impl.*;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * PageDisplayTester tests entry content required to display entry page(s) defined in {@code PageDisplayRequirement}s
 */
public class EntryPageDisplayTester {

    private static final Map<EntryPage, PageDisplayRequirement> ALL_PAGE_REQUIREMENTS;

    private final Entry entry;
    private final Map<EntryPage, PageDisplayRequirement> registeredPageDisplayRequirements;

    static {
        ALL_PAGE_REQUIREMENTS = new EnumMap<>(EntryPage.class);
        ALL_PAGE_REQUIREMENTS.put(EntryPage.EXONS, ExonsPageDisplayRequirement.getInstance());
        ALL_PAGE_REQUIREMENTS.put(EntryPage.EXPRESSION, ExpressionPageDisplayRequirement.getInstance());
        ALL_PAGE_REQUIREMENTS.put(EntryPage.FUNCTION, FunctionPageDisplayRequirement.getInstance());
        ALL_PAGE_REQUIREMENTS.put(EntryPage.GENE_IDENTIFIERS, GeneIdentifiersPageDisplayRequirement.getInstance());
        ALL_PAGE_REQUIREMENTS.put(EntryPage.PROTEIN_IDENTIFIERS, IdentifiersPageDisplayRequirement.getInstance());
        ALL_PAGE_REQUIREMENTS.put(EntryPage.INTERACTIONS, InteractionsPageDisplayRequirement.getInstance());
        ALL_PAGE_REQUIREMENTS.put(EntryPage.LOCALISATION, LocalisationPageDisplayRequirement.getInstance());
        ALL_PAGE_REQUIREMENTS.put(EntryPage.MEDICAL, MedicalPageDisplayRequirement.getInstance());
        ALL_PAGE_REQUIREMENTS.put(EntryPage.PEPTIDES, PeptidesPageDisplayRequirement.getInstance());
        ALL_PAGE_REQUIREMENTS.put(EntryPage.PHENOTYPES, PhenotypesPageDisplayRequirement.getInstance());
        ALL_PAGE_REQUIREMENTS.put(EntryPage.PROTEOMICS, ProteomicsPageDisplayRequirement.getInstance());
        ALL_PAGE_REQUIREMENTS.put(EntryPage.SEQUENCE, SequencePageDisplayRequirement.getInstance());
        ALL_PAGE_REQUIREMENTS.put(EntryPage.STRUCTURES, StructuresPageDisplayRequirement.getInstance());
    }

    EntryPageDisplayTester(Entry entry) {

        Objects.requireNonNull(entry);

        this.entry = entry;
        registeredPageDisplayRequirements = new EnumMap<>(EntryPage.class);
    }

    /**
     * Unique entry point to build instance of this class
     * @param entry the entry to test for page display
     * @return an instance of tester
     */
    public static EntryPageDisplayTester allPageRequirements(Entry entry) {

        EntryPageDisplayTester entryPageDisplayTester = new EntryPageDisplayTester(entry);

        ALL_PAGE_REQUIREMENTS.values().forEach(entryPageDisplayTester::addRequirement);

        return entryPageDisplayTester;
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
