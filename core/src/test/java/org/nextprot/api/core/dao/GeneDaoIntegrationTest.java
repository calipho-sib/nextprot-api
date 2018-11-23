package org.nextprot.api.core.dao;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.core.domain.ChromosomalLocation;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@ActiveProfiles({ "dev" })
public class GeneDaoIntegrationTest extends CoreUnitBaseTest {

	@Autowired private GeneDAO geneDao;
	
	private ChromosomalLocation buildChromosomalLocation(String displayName, String geneGeneNames, String masterGeneNames) {
		ChromosomalLocation loc = new ChromosomalLocation();
		loc.setDisplayName(displayName);
		loc.setGeneGeneNames(geneGeneNames);
		loc.setMasterGeneNames(masterGeneNames);
		return loc;
	}
	
	@Test
	public void shouldHandleCaseWhenNoUniProtNameIsProvided() { 
		
		// Case 1 - about 167 locations, example: NX_Q8NDZ9;
		ChromosomalLocation loc = buildChromosomalLocation("RP11-138I18.1", "RP11-138I18.1", null);		
		Assert.assertEquals("RP11-138I18.1",loc.getRecommendedName("old"));
		Assert.assertEquals("unknown",loc.getRecommendedName());
	}
	
	@Test
	public void shouldHandleCaseWhenASingleUniProtNameIsProvided() { 
		
		// Case 2 - about 21685 locations, example: NX_Q8WVF5;
		ChromosomalLocation loc = buildChromosomalLocation("KCTD4-dn", "KCTD4-gn", "KCTD4");		
		Assert.assertEquals("KCTD4",loc.getRecommendedName("old"));
		Assert.assertEquals("KCTD4",loc.getRecommendedName());
	}

	// TODO: TO FIX BY PAM
	@Ignore
	@Test
	public void shouldHandleCaseWhenSomeOfMultipleUniProtNamesMatchesAreProvided() { 
		
		// Case 3 - about 237 locations
		ChromosomalLocation loc;
		
		// (a) In this example: NX_O14603, 2 gene names are in both geneGeneNameas and masterGeneNames: PRY2 PRY => NOT deterministic
		// getRecommendedName() returns the first found in the intersection, it is arbitrary, 
		// the displayName could in this case make the choice deterministic
	
		loc = buildChromosomalLocation("PRY2", "PRY PRY2", "PRYP4 PRYP3 PRY2 PRY");		
		Assert.assertEquals("PRY2",loc.getRecommendedName("old"));
		Assert.assertEquals("PRY",loc.getRecommendedName());
		
		// (b) In this example: NX_Q0WX57, a single gene name is both in geneGeneNameas and masterGeneNames: USP17L30 => deterministic.
		// But we see that the ENSG gene name (displayName) prefers the other geneName USP17J
		loc = buildChromosomalLocation("USP17J", "USP17J USP17L30", "USP17L30 USP17L25 USP17L29 USP17L28 USP17L27 USP17L26 USP17L24");		
		Assert.assertEquals("USP17L30",loc.getRecommendedName("old"));
		Assert.assertEquals("USP17L30",loc.getRecommendedName());
		
		// (c) In this example NX_Q13066: no gene name are both in geneGeneNameas and masterGeneNames => NOT deterministic
		// We choose the first one in masterGeneNames
		loc = buildChromosomalLocation(null, null, "GAGE2C GAGE2B");		
		Assert.assertEquals("GAGE2C",loc.getRecommendedName("old"));
		Assert.assertEquals("GAGE2C",loc.getRecommendedName());
		
		loc = buildChromosomalLocation("fakeGene", "fakeGene", "GAGE2C GAGE2B");		
		Assert.assertEquals("GAGE2C",loc.getRecommendedName("old"));
		Assert.assertEquals("GAGE2C",loc.getRecommendedName());
		
		loc = buildChromosomalLocation("fakeGene", "fakeGene fakeGene2", "GAGE2C GAGE2B");		
		Assert.assertEquals("GAGE2C",loc.getRecommendedName("old"));
		Assert.assertEquals("GAGE2C",loc.getRecommendedName());
		
	}
	
	@Test
	public void shouldShowDifferenceBetweenOldAndNewImplementation() { 
		
		ChromosomalLocation loc;

		// about 103 locations: Example: NX_A6NJ64
		// The displayName is null and displayName is not null
		loc = buildChromosomalLocation("RP11-166B2.1", "RP11-166B2.1", null);		
		Assert.assertEquals("RP11-166B2.1",loc.getRecommendedName("old"));
		Assert.assertEquals("unknown",loc.getRecommendedName());

		// TODO find other example
		// about 3 locations: Example: NX_Q5VWM5 
		// The displayName is the second option of the masterGeneNames and the second option of the geneGeneNames
		loc = buildChromosomalLocation("PRAMEF15", "PRAMEF9 PRAMEF15", "PRAMEF9 PRAMEF15");		
		Assert.assertEquals("PRAMEF15",loc.getRecommendedName("old"));
		Assert.assertEquals("PRAMEF15",loc.getRecommendedName());
		
	}
	
	@Test
	public void shouldGetSameValuesFromOldAndNewQueries() { 
		
		String entryName = "NX_A6NCW0";
		List<ChromosomalLocation> locs1;
		ChromosomalLocation loc1;
		List<ChromosomalLocation> locs2;
		ChromosomalLocation loc2;
		
		// old algorithm
		locs1 = geneDao.findChromosomalLocationsByEntryNameOld(entryName);
		Assert.assertEquals(1, locs1.size());
		loc1 = locs1.get(0);
		
		// new algorithm
		locs2 = geneDao.findChromosomalLocationsByEntryName(entryName);
		Assert.assertEquals(1, locs2.size());
		loc2 = locs1.get(0);
		
		// field content comparison
		Assert.assertEquals(loc1.getAccession(), loc2.getAccession());
		Assert.assertEquals(loc1.getBand(), loc2.getBand());
		Assert.assertEquals(loc1.getChromosome(), loc2.getChromosome());
		Assert.assertEquals(loc1.isGoldMapping(),loc2.isGoldMapping());
		Assert.assertEquals(loc1.getRecommendedName("old"), loc2.getRecommendedName("old"));
		Assert.assertEquals(loc1.getFirstPosition(), loc2.getFirstPosition());
		Assert.assertEquals(loc1.getLastPosition(), loc2.getLastPosition());
		Assert.assertEquals(loc1.getStrand(), loc2.getStrand());
		
	}
	
	@Test
	public void shouldGetSomeSilverGeneMappingLocation() { 
		
		// Note that SILVER mapping should be filtered out in chromosome reports
		
		// We just check that we have one SILVER chr location in these examples (should hopefully remain true a long time)

		// Find more examples with query "Query for case 1" in
		// See https://docs.google.com/document/d/1SkyXg82DgvbRD6Xq5tye1Ay87KCYF1D7VQBDB1A94iQ/edit#
		// just replace with condition for case 5:
		// where 
		// gi_src = 'Ensembl' and haplotype = 'haplotype' and map_mastbool is true and map_qid = 50 
		
		String[] entryNames = {"NX_P30450", "NX_P30453", "NX_Q16473", "NX_P30462", "NX_P30456" };
		
		boolean haveSilverMapping = false;
		for (String entryName: entryNames) {
			int cnt=0; 
			List<ChromosomalLocation> locs = geneDao.findChromosomalLocationsByEntryName(entryName);
			for (ChromosomalLocation loc: locs) {
				if (! loc.isGoldMapping()) {
					haveSilverMapping=true;
					cnt++;		
				}
			}
			//System.out.println(entryName + ":" + locs.size() + " ChrLocations, " + cnt + " SILVER mapping");
		}
		Assert.assertEquals(true, haveSilverMapping);
	}
}
