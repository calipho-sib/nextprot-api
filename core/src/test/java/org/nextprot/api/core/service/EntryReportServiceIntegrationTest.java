package org.nextprot.api.core.service;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.core.domain.EntryReport;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({ "dev" })
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
	public void NX_Q9Y676ShouldHave1GoldOnnlyChromosomalLocation() {

		List<EntryReport> reports = entryReportService.reportEntry("NX_Q9Y676");

		Assert.assertEquals(1, reports.size());
	}

	@Test
	public void NX_Q9Y676ShouldHave2GoldOnnlyChromosomalLocation() {

		List<EntryReport> reports = entryReportService.reportEntry("NX_A6NER0");

		Assert.assertEquals(2, reports.size());
	}

	@Test
	public void NX_Q9Y676ShouldHaveAlso2GoldOnnlyChromosomalLocation() {

		List<EntryReport> reports = entryReportService.reportEntry("NX_Q9H239");

		Assert.assertEquals(2, reports.size());
	}
	
	@Ignore 
	@Test // ok on np_20170413
	public void TheseShouldHaveProteomicsFalse() {

		List<String> negEntries = Arrays.asList("NX_P50052", "NX_Q8WXH6", "NX_O15255", "NX_Q9UJ90", "NX_Q8NG92");
		int errCnt=0;
		for (String ac:negEntries) {
			List<EntryReport> reports = entryReportService.reportEntry(ac);
			if (reports.get(0).isProteomics()==true) {
				errCnt++;
				System.out.println("ERROR: " + ac + " proteomics should be false");
			} else {
				System.out.println("OK: " + ac + " proteomics is false");
			}
			Assert.assertEquals(0, errCnt);			
		}
		
	}
	
	@Ignore 
	@Test // ok on np_20170413
	public void NX_P46019ShouldHaveProteomicsTrue() {

		List<EntryReport> reports = entryReportService.reportEntry("NX_P46019"); // has nextprot PTM
		Assert.assertEquals(1, reports.size());
		Assert.assertEquals(true, reports.get(0).isProteomics());
		
	}
	
	
	@Ignore 
	@Test // ok on np_20170413
    public void TheseShouldHaveProteomicsTrue() {  
    	
		List<String> negEntries = Arrays.asList("NX_P0CW71", "NX_O43320","NX_Q07326", "NX_Q9H5Z6","NX_P29459","NX_Q96MM3","NX_Q86UD3");
		int errCnt=0;
		for (String ac:negEntries) {
			List<EntryReport> reports = entryReportService.reportEntry(ac);
			if (reports.get(0).isProteomics()==false) {
				errCnt++;
				System.out.println("ERROR: " + ac + " proteomics should be true");
			} else {
				System.out.println("OK: " + ac + " proteomics is true");
			}
			Assert.assertEquals(0, errCnt);			
		}

    }

	
	
}
