package org.nextprot.api.core.service;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.core.domain.EntryReport;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

@ActiveProfiles({ "dev","cache" })
public class EntryReportStatsServiceIntegrationTest extends CoreUnitBaseTest {

	@Autowired
	private EntryGeneReportService entryGeneReportService;

	@Test
	public void NX_Q9Y6F7ShouldHave1GeneWith2ChromosomalLocationsAtDifferentDNAStrands() {

		List<EntryReport> reports = entryGeneReportService.reportEntry("NX_Q9Y6F7");

		Assert.assertEquals(2, reports.size());

		for (EntryReport report : reports) {
			Assert.assertEquals("NX_Q9Y6F7", report.getAccession());
			Assert.assertEquals("CDY2A", report.getGeneName());
			Assert.assertEquals("Yq11.222", report.getChromosomalLocation());
		}
	}

	@Test
	public void NX_Q9Y676ShouldHave1GoldOnnlyChromosomalLocation() {

		List<EntryReport> reports = entryGeneReportService.reportEntry("NX_Q9Y676");

		Assert.assertEquals(1, reports.size());
	}

	@Test
	public void NX_Q9Y676ShouldHave2GoldOnnlyChromosomalLocation() {

		List<EntryReport> reports = entryGeneReportService.reportEntry("NX_A6NER0");

		Assert.assertEquals(1, reports.size());
	}

	@Test
	public void NX_Q9Y676ShouldHaveAlso2GoldOnnlyChromosomalLocation() {

		List<EntryReport> reports = entryGeneReportService.reportEntry("NX_Q9H239");

		Assert.assertEquals(1, reports.size());
	}

    @Test
    public void NX_P01308ShouldHaveAlsoPubInfos() {

        List<EntryReport> reports = entryGeneReportService.reportEntry("NX_P01308");
        Assert.assertEquals(3, reports.get(0).countWebResources());
        Assert.assertEquals(4, reports.get(0).countSubmissions());
        Assert.assertEquals(0, reports.get(0).countPatents());
        Assert.assertEquals(692, reports.get(0).countAdditionalPublications());
        Assert.assertEquals(99, reports.get(0).countCuratedPublications());
    }

	@Ignore 
	@Test // ok on np_20170413
	public void TheseShouldHaveProteomicsFalse() {

		List<String> negEntries = Arrays.asList("NX_P50052", "NX_Q8WXH6", "NX_O15255", "NX_Q9UJ90", "NX_Q8NG92");
		int errCnt=0;
		for (String ac:negEntries) {
			List<EntryReport> reports = entryGeneReportService.reportEntry(ac);
			if (reports.get(0).isProteomics()==true) {
				errCnt++;
			}
		}
		Assert.assertEquals(0, errCnt);			
		
	}
	
	@Ignore 
	@Test // ok on np_20170413
	public void NX_P46019ShouldHaveProteomicsTrue() {

		List<EntryReport> reports = entryGeneReportService.reportEntry("NX_P46019"); // has nextprot PTM
		Assert.assertEquals(1, reports.size());
		Assert.assertEquals(true, reports.get(0).isProteomics());
		
	}

	@Ignore 
	@Test // ok on np_20170413
    public void TheseShouldAlsoHaveProteomicsTrue() {  
    	
		List<String> posEntries = Arrays.asList(
				"NX_Q07326","NX_P29459","NX_A1E959","NX_Q86UD3","NX_Q5DT21","NX_P10265","NX_Q8IZT8","NX_P81534","NX_P81534",
				"NX_Q6JVE6","NX_P31314","NX_Q7Z5B4","NX_Q16621","NX_Q6UVW9","NX_P0DMR3","NX_P36544","NX_P80075","NX_Q99616",
				"NX_Q8IYD9","NX_B3KS81","NX_Q2I0M5","NX_O43320","NX_P0CW71");
		int errCnt=0;
		for (String ac:posEntries) {
			//if (ac.equals("NX_P29459")) break;
			List<EntryReport> reports = entryGeneReportService.reportEntry(ac);
			if (reports.get(0).isProteomics()==false) {
				errCnt++;
			}
		}
		Assert.assertEquals(0, errCnt);			
    }

	@Ignore 
	@Test // ok on np_20170413
    public void TheseShouldHaveProteomicsTrue() {  
    	
		List<String> negEntries = Arrays.asList("NX_P0CW71", "NX_O43320","NX_Q07326", "NX_Q9H5Z6","NX_P29459","NX_Q96MM3","NX_Q86UD3");
		int errCnt=0;
		for (String ac:negEntries) {
			List<EntryReport> reports = entryGeneReportService.reportEntry(ac);
			if (reports.get(0).isProteomics()==false) {
				errCnt++;
			}
		}
		Assert.assertEquals(0, errCnt);			
    }

	
 
	@Ignore 
	@Test // ok on np_20170413
    public void TheseShouldHaveAntibodyTrue() {  
    	
		List<String> negEntries = Arrays.asList("NX_P03886","NX_P16949","NX_P09326","NX_P84077","NX_Q13291", "NX_O43488", "NX_Q5T7P8");
		int errCnt=0;
		for (String ac:negEntries) {
			List<EntryReport> reports = entryGeneReportService.reportEntry(ac);
			if (reports.get(0).isAntibody()==false) {
				errCnt++;
				//System.out.println("ERROR: " + ac + " antibody should be true");
			} else {
				//System.out.println("OK: " + ac + " antibody is true");
			}
		}
		Assert.assertEquals(0, errCnt);			

    }

	// NP2 implementation is correct. see https://issues.isb-sib.ch/browse/NEXTPROT-1479
	// on np_20170413, these entries should NOT appear in file HPP_entries_with_phosph_by_chromosome.txt
	private List<String> entriesWithPhosphoTrueInNP1ButFalseInNP2 = Arrays.asList(
			"NX_Q5TC12","NX_Q9NZV5","NX_Q5EBM0","NX_Q96PE7","NX_P12235","NX_O75439","NX_Q92537","NX_A6NH21","NX_Q9Y2R9","NX_Q9Y3E5",
			"NX_P05141","NX_P12236");

	// NP2 implementation is correct. see https://issues.isb-sib.ch/browse/NEXTPROT-1479
	// on np_20170413, these entries should NOT appear in file HPP_entries_with_nacetyl_by_chromosome.txt
	private List<String> entriesWithNacetylTrueInNP1ButFalseInNP2 = Arrays.asList(
			"NX_O00750","NX_Q86SQ9","NX_Q96BN2","NX_O43255","NX_Q9NVJ2","NX_P36406","NX_Q5VYS8","NX_Q13490","NX_Q7LDG7","NX_P55160",
			"NX_Q9NWS1","NX_Q8NBM4","NX_Q92802","NX_Q9Y2K1","NX_Q16659","NX_Q96NB1","NX_Q9NQC7","NX_O75674","NX_Q13829","NX_Q96C03",
			"NX_Q96GD4","NX_Q7LBR1","NX_P49427","NX_Q969E2","NX_Q96BX8","NX_Q9NVP2","NX_Q9UI14","NX_Q9BZJ0","NX_Q9H5Z1","NX_P55854",
			"NX_P57075","NX_P98170","NX_Q6PEV8");
	
	
	// NP2 implementation is correct. 
	// on np_20170413 these entries have a BRONZE CAB antibody which is taken by error into account in old NP1 implementation
	private List<String>entriesToBeSkippedOnTestingAntibodyFlag = Arrays.asList(
			"NX_Q15583","NX_P55895","NX_Q9BZB8","NX_Q9H2X0","NX_P21439","NX_P43146","NX_Q02817","NX_Q96DT0","NX_Q6P1K2","NX_O00631",
			"NX_P57086","NX_O95838","NX_O95954","NX_P15172","NX_P23468","NX_Q03431","NX_Q14213","NX_Q14571","NX_Q7LC44","NX_P41236",
			"NX_P55017","NX_Q8NFJ5","NX_P78509","NX_Q13733","NX_P11150","NX_Q92838","NX_O14958","NX_P09544","NX_P11802","NX_Q14623",
			"NX_P42695","NX_Q07011","NX_Q8N3H0","NX_Q9H3M7","NX_O94907","NX_O60760","NX_Q86XW9","NX_P28335","NX_Q9UJ72","NX_P48023",
			"NX_Q6P9H4","NX_P46695","NX_Q92736","NX_Q9UHE8","NX_P78333","NX_Q15007","NX_O14757","NX_Q6XE24","NX_Q16650","NX_P09211",
			"NX_Q9BR10","NX_P01344","NX_P35212","NX_Q8TD84","NX_P08254","NX_P51164","NX_Q9P2E2","NX_O15083","NX_P78508","NX_Q9UKX3");
	
	
	
	
	
}
