package org.nextprot.api.web.service;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;


public class EntryPageServiceTest extends WebIntegrationBaseTest {

	@Autowired
	private EntryPageService entryPageService;
	@Autowired 
	EntryBuilderService entryBuilderService;

	@Test
	public void testPageViewDisplay() {

		Map<String, Boolean> report = entryPageService.hasContentForPageDisplay("NX_P01308");

		Assert.assertTrue(!report.get("Phenotypes"));
		Assert.assertTrue(report.get("Peptides"));
		Assert.assertTrue(!report.get("Exons"));
		Assert.assertTrue(report.get("Localization"));
		Assert.assertTrue(report.get("Identifiers"));
		Assert.assertTrue(report.get("Interactions"));
		Assert.assertTrue(report.get("Structures"));
		Assert.assertTrue(report.get("Medical"));
		Assert.assertTrue(report.get("Proteomics"));
		Assert.assertTrue(report.get("Expression"));
		Assert.assertTrue(report.get("Gene Identifiers"));
		Assert.assertTrue(report.get("Function"));
		Assert.assertTrue(report.get("Sequence"));
	}

	@Test
	public void testFilterEntryContentInPageView() {

		String entryAC = "NX_P02649";
		Entry originalEntry = entryBuilderService.build(EntryConfig.newConfig(entryAC).withEverything());

		Entry filteredEntry;
		
		filteredEntry = entryPageService.filterXrefInPageView(entryAC, "sequence");
		Assert.assertTrue(originalEntry.getXrefs().size() > filteredEntry.getXrefs().size());
		// uniprot xref should be present
		Assert.assertTrue(
				filteredEntry.getXrefs().stream().anyMatch(x -> 
					x.getDatabaseName().equals("UniProt") && 
					x.getAccession().equals(originalEntry.getUniprotName()))
		);

		filteredEntry = entryPageService.filterXrefInPageView(entryAC, "expression");
		Assert.assertTrue(originalEntry.getXrefs().size() > filteredEntry.getXrefs().size());
		// uniprot xref should NOT be present
		Assert.assertFalse(
				filteredEntry.getXrefs().stream().anyMatch(x -> 
					x.getDatabaseName().equals("UniProt") && 
					x.getAccession().equals(originalEntry.getUniprotName()))
		);
	}

	@Test
	public void proteomicPageShouldFilterPhosphoSitePlusDb() {

		String entryAC = "NX_P52701";
		Entry filteredEntry = entryPageService.filterXrefInPageView(entryAC, "proteomics");
		Assert.assertTrue(filteredEntry.getXrefs().stream()
                .anyMatch(x -> x.getDatabaseName().equals("PhosphoSitePlus"))
        );
	}
}
