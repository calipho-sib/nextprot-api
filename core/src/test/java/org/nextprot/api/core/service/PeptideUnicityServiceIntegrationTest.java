package org.nextprot.api.core.service;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.PeptideUnicity;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({ "dev","cache" })
public class PeptideUnicityServiceIntegrationTest extends CoreUnitBaseTest{
        
    @Autowired private PeptideUnicityService peptideUnicityService;
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
    	PeptideUnicity result = peptideUnicityService.getPeptideUnicityFromMappingIsoforms(isoset);
    	Assert.assertEquals(PeptideUnicity.Value.UNIQUE, result.getValue());
   }

    @Test
    public void testUniqueCase2() {
    	Set<String> isoset = new TreeSet<String>(Arrays.asList("NX_ENTRY1-1","NX_ENTRY1-2"));
    	PeptideUnicity result = peptideUnicityService.getPeptideUnicityFromMappingIsoforms(isoset);
    	Assert.assertEquals(PeptideUnicity.Value.UNIQUE, result.getValue());
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
    	PeptideUnicity result = peptideUnicityService.getPeptideUnicityFromMappingIsoforms(isoset);
    	Assert.assertEquals(PeptideUnicity.Value.NOT_UNIQUE, result.getValue());
   }
    
    @Test
    public void testNonUniqueCase2() {
    	Set<String> isoset = new TreeSet<String>(Arrays.asList("NX_ENTRY1-1","NX_ENTRY2-1","NX_ENTRY2-2"));
    	PeptideUnicity result = peptideUnicityService.getPeptideUnicityFromMappingIsoforms(isoset);
    	Assert.assertEquals(PeptideUnicity.Value.NOT_UNIQUE, result.getValue());
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
    	PeptideUnicity result = peptideUnicityService.getPeptideUnicityFromMappingIsoforms(isoset);
    	Assert.assertEquals(PeptideUnicity.Value.PSEUDO_UNIQUE, result.getValue());
    	Assert.assertEquals(isoset, result.getEquivalentIsoforms());
   }
    
    @Test
    public void testPseudoUniqueCase2() {
    	// first 2 isoforms have same sequence (same md5) and other isoforms belong to same entry => PSEUDO UNIQUE
    	Set<String> isoset = new TreeSet<String>(Arrays.asList("NX_P0DN79-1","NX_P35520-1","NX_P35520-2","NX_P35520-3"));  
    	PeptideUnicity result = peptideUnicityService.getPeptideUnicityFromMappingIsoforms(isoset);
    	Assert.assertEquals(PeptideUnicity.Value.PSEUDO_UNIQUE, result.getValue());
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
    	PeptideUnicity result;
    	
    	// first call
    	t0 = System.currentTimeMillis();
    	Set<String> isoset = new TreeSet<String>(Arrays.asList("NX_P0DN79-1","NX_P35520-1")); 
    	result = peptideUnicityService.getPeptideUnicityFromMappingIsoforms(isoset);
    	Assert.assertEquals(PeptideUnicity.Value.PSEUDO_UNIQUE, result.getValue());
    	long tFirst = System.currentTimeMillis()-t0;
    	
    	// loop on 100 calls 
    	t0 = System.currentTimeMillis();
    	for (int i=0;i<100;i++) {
    		String iso = "NX_P35520-" + (i+2);
    		isoset.add(iso);
        	result = peptideUnicityService.getPeptideUnicityFromMappingIsoforms(isoset);
        	Assert.assertEquals(PeptideUnicity.Value.PSEUDO_UNIQUE, result.getValue());
    	}
    	long tNext100 = System.currentTimeMillis()-t0;

    	System.out.println("time for very first call: " + tFirst +   "[ms]");
    	System.out.println("time for next 100  calls: " + tNext100 + "[ms]");
    	Assert.assertTrue(tFirst > tNext100);
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

    	// clear related cache to make sure we have no PeptideUnicity serialization version conflict
    	cacheManager.getCache("peptide-name-unicity-map").clear();
    	
    	PeptideUnicity pu;
    	Set<String> expectedEquivalentIsoSet;
    	long t0;
    	
    	// first call
    	t0 = System.currentTimeMillis();
    	pu = peptideUnicityService.getPeptideNameUnicityMap().get("NX_PEPT01668698"); // [NX_P02771-1]
    	Assert.assertEquals(pu.getValue(), PeptideUnicity.Value.UNIQUE);
    	long tFirst = System.currentTimeMillis()-t0;
    	    	
    	// subsequent calls should use cache
    	t0 = System.currentTimeMillis();
    	pu = peptideUnicityService.getPeptideNameUnicityMap().get("NX_PEPT01410369"); // maps [NX_P02679-1, NX_P02679-2] 
    	Assert.assertEquals(pu.getValue(), PeptideUnicity.Value.UNIQUE);
    	
    	
    	pu = peptideUnicityService.getPeptideNameUnicityMap().get("NX_PEPT00361176"); // maps [NX_O14815-1, NX_O14815-2, NX_P07384-1, NX_P17655-1, NX_P17655-2]  
    	Assert.assertEquals(pu.getValue(), PeptideUnicity.Value.NOT_UNIQUE);
    	
    	pu = peptideUnicityService.getPeptideNameUnicityMap().get("NX_PEPT00000054"); // maps [NX_B9A064-1, NX_P0CF74-1, NX_P0CG04-1, NX_P0DOY2-1, NX_P0DOY3-1]
    	Assert.assertEquals(pu.getValue(), PeptideUnicity.Value.NOT_UNIQUE);
    	
    	
    	pu = peptideUnicityService.getPeptideNameUnicityMap().get("NX_PEPT01978634"); // maps [NX_P0DMV8-1, NX_P0DMV8-2, NX_P0DMV9-1] 
    	Assert.assertEquals(pu.getValue(), PeptideUnicity.Value.PSEUDO_UNIQUE);
    	// SOME mapped isoforms are equivalent: [NX_P0DMV8-1, NX_P0DMV9-1]
    	expectedEquivalentIsoSet = new TreeSet<>(Arrays.asList("NX_P0DMV8-1", "NX_P0DMV9-1"));
    	Assert.assertEquals(expectedEquivalentIsoSet, pu.getEquivalentIsoforms());
    	
    	pu = peptideUnicityService.getPeptideNameUnicityMap().get("NX_PEPT01888037"); // maps [NX_P0DP23-1, NX_P0DP24-1, NX_P0DP25-1] 
    	Assert.assertEquals(pu.getValue(), PeptideUnicity.Value.PSEUDO_UNIQUE);
    	// equivalent isoforms: ALL mapped isoforms are equivalent: [NX_P0DP23-1, NX_P0DP24-1, NX_P0DP25-1]
    	expectedEquivalentIsoSet = new TreeSet<>(Arrays.asList("NX_P0DP23-1", "NX_P0DP24-1", "NX_P0DP25-1"));
    	Assert.assertEquals(expectedEquivalentIsoSet, pu.getEquivalentIsoforms());
    	long tNext = System.currentTimeMillis()-t0;

    	System.out.println("time for very first call: " + tFirst +   "[ms]");
    	System.out.println("time for next      calls: " + tNext + "[ms]");
    	Assert.assertTrue(tFirst > tNext);
    	
    }
    
    
}