package org.nextprot.api.web.service;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;


public class EntryPageServiceTest extends WebIntegrationBaseTest {

	@Autowired
	private EntryPageService entryPageService;

	@Test
	public void testPageViewDisplay() {

		Map<String, Boolean> report = entryPageService.testEntryContentForPageDisplay("NX_P52701");

		Assert.assertTrue(!report.get("Phenotypes"));
		Assert.assertTrue(!report.get("Peptides"));
		Assert.assertTrue(!report.get("Exons"));
		Assert.assertTrue(!report.get("Localization"));
		Assert.assertTrue(report.get("Identifiers"));
		Assert.assertTrue(report.get("Interactions"));
		Assert.assertTrue(report.get("Structures"));
		Assert.assertTrue(report.get("Medical"));
		Assert.assertTrue(report.get("Proteomics"));
		Assert.assertTrue(report.get("Expression"));
		Assert.assertTrue(report.get("Proteomics"));
		Assert.assertTrue(report.get("Gene Identifiers"));
		Assert.assertTrue(report.get("Function"));
		Assert.assertTrue(report.get("Sequence"));
	}
}
