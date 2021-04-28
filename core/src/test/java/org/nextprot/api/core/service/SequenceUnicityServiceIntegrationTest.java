package org.nextprot.api.core.service;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.core.domain.SequenceUnicity;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

@ActiveProfiles({ "dev","cache" })
public class SequenceUnicityServiceIntegrationTest extends CoreUnitBaseTest{
        
    @Autowired private SequenceUnicityService sequenceUnicityService;
	@Autowired private CacheManager cacheManager;


    
/* 
 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
 * 
 * UNIQUE cases
 * 
 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
 */
    
    @Test
    public void testUniqueCase1() {
    	Set<String> isoset = new TreeSet<String>(Arrays.asList("NX_ENTRY1-1"));
    	SequenceUnicity result = sequenceUnicityService.getSequenceUnicityFromMappingIsoforms(isoset);
    	Assert.assertEquals(SequenceUnicity.Value.UNIQUE, result.getValue());
   }

    @Test
    public void testUniqueCase2() {
    	Set<String> isoset = new TreeSet<String>(Arrays.asList("NX_ENTRY1-1","NX_ENTRY1-2"));
    	SequenceUnicity result = sequenceUnicityService.getSequenceUnicityFromMappingIsoforms(isoset);
    	Assert.assertEquals(SequenceUnicity.Value.UNIQUE, result.getValue());
   }

/* 
 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
 * 
 * NON_UNIQUE cases
 * 
 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
 */
    @Test
    public void testNonUniqueCase1() {
    	Set<String> isoset = new TreeSet<String>(Arrays.asList("NX_ENTRY1-1","NX_ENTRY2-1"));
    	SequenceUnicity result = sequenceUnicityService.getSequenceUnicityFromMappingIsoforms(isoset);
    	Assert.assertEquals(SequenceUnicity.Value.NOT_UNIQUE, result.getValue());
   }
    
    @Test
    public void testNonUniqueCase2() {
    	Set<String> isoset = new TreeSet<String>(Arrays.asList("NX_ENTRY1-1","NX_ENTRY2-1","NX_ENTRY2-2"));
    	SequenceUnicity result = sequenceUnicityService.getSequenceUnicityFromMappingIsoforms(isoset);
    	Assert.assertEquals(SequenceUnicity.Value.NOT_UNIQUE, result.getValue());
   }
    
/* 
 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
 * 
 * PSEUDO_UNIQUE cases
 * 
 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
 * Each line in this comment contains a list of isoforms that are known to be identical but in different entries
 * If a peptide matches isoforms of multiple entries known as sharing an isoform then the peptide is said PSEUDO_UNIQUE
 * NX_A0A087WW87-1,NX_P01614-1
 * NX_O43812-1,NX_Q96PT3-2
 * NX_Q6S5H4-1,NX_A0JP26-1
 * NX_P0DMU8-1,NX_P0DMV0-1,NX_P0DMU7-1
 * NX_Q01081-1,NX_P0DN76-1
 * NX_B7ZAQ6-1,NX_P0CG08-1
 * NX_B0FP48-1,NX_E5RIL1-1
 * NX_P0DN79-1,NX_P35520-1
 */
    
    @Test
    public void testPseudoUniqueCase1() {
    	// the 2 isoform have same sequence (same md5) => PSEUDO UNIQUE
    	Set<String> isoset = new TreeSet<String>(Arrays.asList("NX_P0DN79-1","NX_P35520-1"));  
    	SequenceUnicity result = sequenceUnicityService.getSequenceUnicityFromMappingIsoforms(isoset);
    	Assert.assertEquals(SequenceUnicity.Value.PSEUDO_UNIQUE, result.getValue());
    	Assert.assertEquals(isoset, result.getEquivalentIsoforms());
   }
    
    @Test
    public void testPseudoUniqueCase2() {
    	// first 2 isoforms have same sequence (same md5) and other isoforms belong to same entry => PSEUDO UNIQUE
    	Set<String> isoset = new TreeSet<String>(Arrays.asList("NX_P0DN79-1","NX_P35520-1","NX_P35520-2","NX_P35520-3"));  
    	SequenceUnicity result = sequenceUnicityService.getSequenceUnicityFromMappingIsoforms(isoset);
    	Assert.assertEquals(SequenceUnicity.Value.PSEUDO_UNIQUE, result.getValue());
    	Set<String> expEquivSet = new TreeSet<String>(Arrays.asList("NX_P0DN79-1","NX_P35520-1")); 
    	Assert.assertEquals(expEquivSet, result.getEquivalentIsoforms());
   }
    
    /* 
     * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
     * 
     * multiple PSEUDO_UNIQUE cases to see if cache of isoform service is effective 
     * 
     * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
     */
    @Test
    public void testMultiPseudoUnique() {
    	// the 2 isoform have same sequence (same md5) => PSEUDO UNIQUE
    	long t0;
    	SequenceUnicity result;
    	
    	// first call
    	t0 = System.currentTimeMillis();
    	Set<String> isoset = new TreeSet<String>(Arrays.asList("NX_P0DN79-1","NX_P35520-1")); 
    	result = sequenceUnicityService.getSequenceUnicityFromMappingIsoforms(isoset);
    	Assert.assertEquals(SequenceUnicity.Value.PSEUDO_UNIQUE, result.getValue());
    	long tFirst = System.currentTimeMillis()-t0;
    	
    	// loop on 100 calls 
    	t0 = System.currentTimeMillis();
    	for (int i=0;i<100;i++) {
    		String iso = "NX_P35520-" + (i+2);
    		isoset.add(iso);
        	result = sequenceUnicityService.getSequenceUnicityFromMappingIsoforms(isoset);
        	Assert.assertEquals(SequenceUnicity.Value.PSEUDO_UNIQUE, result.getValue());
    	}
    	long tNext100 = System.currentTimeMillis()-t0;

//    	System.out.println("time for very first call: " + tFirst +   "[ms]");
//    	System.out.println("time for next 100  calls: " + tNext100 + "[ms]");
    	// line below ok only if cache is cleared before starting the test
    	//Assert.assertTrue(tFirst > tNext100);
    }


    /* 
     * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
     * 
     * 
     * 
     * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
     */
    @Test
    public void tesUnicityOfSomeKnownPeptides() {

    	// clear related cache to make sure we have no SequenceUnicity serialization version conflict
    	cacheManager.getCache("peptide-name-unicity-map").clear();
    	
    	SequenceUnicity pu;
    	Set<String> expectedEquivalentIsoSet;
    	long t0;
    	
    	// first call
    	t0 = System.currentTimeMillis();
    	pu = sequenceUnicityService.getPeptideNameUnicityMap().get("NX_PEPT01668698"); // [NX_P02771-1]
    	Assert.assertEquals(pu.getValue(), SequenceUnicity.Value.UNIQUE);
    	long tFirst = System.currentTimeMillis()-t0;
    	    	
    	// subsequent calls should use cache
    	t0 = System.currentTimeMillis();
    	pu = sequenceUnicityService.getPeptideNameUnicityMap().get("NX_PEPT01410369"); // maps [NX_P02679-1, NX_P02679-2]
    	Assert.assertEquals(pu.getValue(), SequenceUnicity.Value.UNIQUE);
    	
    	
    	pu = sequenceUnicityService.getPeptideNameUnicityMap().get("NX_PEPT00361176"); // maps [NX_O14815-1, NX_O14815-2, NX_P07384-1, NX_P17655-1, NX_P17655-2]
    	Assert.assertEquals(pu.getValue(), SequenceUnicity.Value.NOT_UNIQUE);
    	
    	pu = sequenceUnicityService.getPeptideNameUnicityMap().get("NX_PEPT00000054"); // maps [NX_B9A064-1, NX_P0CF74-1, NX_P0CG04-1, NX_P0DOY2-1, NX_P0DOY3-1]
    	Assert.assertEquals(pu.getValue(), SequenceUnicity.Value.NOT_UNIQUE);
    	
    	
    	pu = sequenceUnicityService.getPeptideNameUnicityMap().get("NX_PEPT01978634"); // maps [NX_P0DMV8-1, NX_P0DMV8-2, NX_P0DMV9-1]
    	Assert.assertEquals(pu.getValue(), SequenceUnicity.Value.PSEUDO_UNIQUE);
    	// SOME mapped isoforms are equivalent: [NX_P0DMV8-1, NX_P0DMV9-1]
    	expectedEquivalentIsoSet = new TreeSet<>(Arrays.asList("NX_P0DMV8-1", "NX_P0DMV9-1"));
    	Assert.assertEquals(expectedEquivalentIsoSet, pu.getEquivalentIsoforms());

		pu = sequenceUnicityService.getPeptideNameUnicityMap().get("NX_PEPT01888037"); // maps [NX_P0DP23-1, NX_P0DP24-1, NX_P0DP25-1]
    	Assert.assertEquals(pu.getValue(), SequenceUnicity.Value.PSEUDO_UNIQUE);
    	// equivalent isoforms: ALL mapped isoforms are equivalent: [NX_P0DP23-1, NX_P0DP24-1, NX_P0DP25-1]
    	expectedEquivalentIsoSet = new TreeSet<>(Arrays.asList("NX_P0DP23-1", "NX_P0DP24-1", "NX_P0DP25-1"));
    	Assert.assertEquals(expectedEquivalentIsoSet, pu.getEquivalentIsoforms());
    	long tNext = System.currentTimeMillis()-t0;
    	
//    	System.out.println("time for very first call: " + tFirst +   "[ms]");
//    	System.out.println("time for next      calls: " + tNext + "[ms]");
    	// line below ok only if cache is cleared before starting the test
    	//Assert.assertTrue(tFirst > tNext);
    	
    }

    @Ignore
    @Test
    public void saveAntibodyUnicity() throws Exception {
    	String fileName = "/tmp/ab-iso.tsv";
    	BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
    	for (String ab : sequenceUnicityService.getAntibodyNameUnicityMap().keySet()) {
    		StringBuffer sb = new StringBuffer();
    		sb.append(ab + "\t");
    		SequenceUnicity su = sequenceUnicityService.getAntibodyNameUnicityMap().get(ab);
    		sb.append(su.getValue().toString() + "\t");
    		if (su.getEquivalentIsoforms() != null) {
    			for (String iso: su.getEquivalentIsoforms() ) sb.append(iso + "\t");
    		}
    		sb.append("\n");    		
            writer.write(sb.toString());
    	}
    	writer.close();
    }
    
	@Test
	public void tesUnicityOfSomeKnownAntibodies() {

		// clear related cache to make sure we have no SequenceUnicity serialization version conflict
		cacheManager.getCache("antibody-name-unicity-map").clear();

		SequenceUnicity pu;
		Set<String> expectedEquivalentIsoSet;

		// first call
		pu = sequenceUnicityService.getAntibodyNameUnicityMap().get("NX_HPA004810"); // [NX_P13164-1]
		Assert.assertEquals(SequenceUnicity.Value.UNIQUE, pu.getValue());

		// subsequent calls should use cache
		pu = sequenceUnicityService.getAntibodyNameUnicityMap().get("NX_HPA003317"); // maps [NX_P39880-1,NX_P39880-2,NX_P39880-3,NX_P39880-4,NX_P39880-5,NX_P39880-6,NX_Q13948-1,NX_Q13948-2,NX_Q13948-9]
		Assert.assertEquals(SequenceUnicity.Value.NOT_UNIQUE, pu.getValue());

		pu = sequenceUnicityService.getAntibodyNameUnicityMap().get("NX_HPA011403"); // maps [NX_B0FP48-1,NX_E5RIL1-1]
		Assert.assertEquals(SequenceUnicity.Value.PSEUDO_UNIQUE, pu.getValue());
		// SOME mapped isoforms are equivalent: [NX_B0FP48-1	NX_E5RIL1-1]
		expectedEquivalentIsoSet = new TreeSet<>(Arrays.asList("NX_B0FP48-1","NX_E5RIL1-1"));
		Assert.assertEquals(expectedEquivalentIsoSet, pu.getEquivalentIsoforms());

		
//		pu = sequenceUnicityService.getAntibodyNameUnicityMap().get("NX_HPA000162"); // maps [NX_Q99865-1, NX_Q9BPZ2-1, NX_Q9Y657-1]
//!!!	Assert.assertEquals(SequenceUnicity.Value.NOT_UNIQUE, pu.getValue());

//		pu = sequenceUnicityService.getAntibodyNameUnicityMap().get("NX_HPA000162"); // maps [NX_Q99865-1, NX_Q9BPZ2-1, NX_Q9Y657-1]
//!!!	Assert.assertEquals(SequenceUnicity.Value.NOT_UNIQUE, pu.getValue());

//		pu = sequenceUnicityService.getAntibodyNameUnicityMap().get("NX_HPA000387"); // maps [NX_P62760-1, NX_Q9UM19-1]
//!!!	Assert.assertEquals(SequenceUnicity.Value.NOT_UNIQUE, pu.getValue());

//		pu = sequenceUnicityService.getAntibodyNameUnicityMap().get("NX_HPA063308"); // maps [NX_P0DI81-1,NX_P0DI81-2,NX_P0DI81-3,NX_P0DI82-1]
//!!!	Assert.assertEquals(SequenceUnicity.Value.PSEUDO_UNIQUE, pu.getValue());
//		// SOME mapped isoforms are equivalent: [NX_P0DI81-1,NX_P0DI82-1]
//		expectedEquivalentIsoSet = new TreeSet<>(Arrays.asList("NX_P0DI81-1", "NX_P0DI82-1"));
//!!!	Assert.assertEquals(expectedEquivalentIsoSet, pu.getEquivalentIsoforms());

//		pu = sequenceUnicityService.getAntibodyNameUnicityMap().get("NX_HPA050006"); // maps [NX_P86790-1,NX_P86791-1]
//!!!	Assert.assertEquals(SequenceUnicity.Value.PSEUDO_UNIQUE, pu.getValue());
//		// equivalent isoforms: ALL mapped isoforms are equivalent: [NX_P86790-1,NX_P86791-1]
//		expectedEquivalentIsoSet = new TreeSet<>(Arrays.asList("NX_P86790-1", "NX_P86791-1"));
//!!!	Assert.assertEquals(expectedEquivalentIsoSet, pu.getEquivalentIsoforms());

	}
}