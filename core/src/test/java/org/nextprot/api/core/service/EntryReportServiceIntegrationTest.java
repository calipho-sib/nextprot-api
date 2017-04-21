package org.nextprot.api.core.service;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.EntryReport;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@ActiveProfiles({ "dev", "cache" })
public class EntryReportServiceIntegrationTest extends CoreUnitBaseTest {

	@Autowired
	private ChromosomeReportService chromosomeReportService;

	@Autowired
	private EntryReportService entryReportService;

	@Test
	public void NX_Q9Y6F7ShouldHave1GeneWith2ChromosomalLocationsAtDifferentDNAStrands() {

		List<EntryReport> reports = entryReportService.reportEntry("NX_Q9Y6F7");

		Assert.assertEquals(2, reports.size());

		for (EntryReport report : reports) {
			Assert.assertEquals("NX_Q9Y6F7", report.getAccession());
			Assert.assertEquals("CDY2A", report.getGeneName());
			Assert.assertEquals("Yq11.222", report.getChromosomalLocation());
		}
	}

	@Test
	public void NX_Q9Y676ShouldHave1PreferredChromosomalLocation() {

		List<EntryReport> reports = entryReportService.reportEntry("NX_Q9Y676");

		Assert.assertEquals(1, reports.size());
	}
}
