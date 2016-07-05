package org.nextprot.api.tasks.solr.indexer.entry.diff;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.SolrDiffTest;
import org.nextprot.api.tasks.solr.indexer.entry.impl.CVFieldBuilder;
import org.springframework.beans.factory.annotation.Autowired;

public class CVFieldBuilderDiffTest extends SolrDiffTest {

	@Autowired TerminologyService terminologyService;

	@Test
	public void testCVs() {
		String[] test_list = {"NX_Q6H8Q1", "NX_O00116","NX_Q7Z6P3","NX_E5RQL4","NX_O00115","NX_Q7Z6P3",
				"NX_Q7Z713", "NX_P22102", "NX_Q7Z713", "NX_O00116", "NX_Q7Z713", "NX_O15056"};

		for(int i=0; i < test_list.length; i++){ 	testCVs(getEntry(test_list[i])); } 
		//for(int i=0; i < 1; i++){ 	testCVs(getEntry(test_list[i])); } // random entries
		
		//Entry entry = getEntry("NX_P20594");
		//Entry entry = getEntry("NX_P14060");
		//testCVs(entry);
	
	}

	
	public void testCVs(Entry entry) {
		
		String entryName = entry.getUniqueName();

		System.out.println("Testing: " + entryName);
		CVFieldBuilder cfb = new CVFieldBuilder();
		cfb.setTerminologyService(terminologyService);
		cfb.initializeBuilder(entry);
		
		// CV_ACS
		Set<String> expectedCVs = new TreeSet<String>((List) getValueForFieldInCurrentSolrImplementation(entryName, Fields.CV_ACS));
		Set<String> CvSet = new TreeSet<String>(cfb.getFieldValue(Fields.CV_ACS, List.class));
		if(CvSet.size() > expectedCVs.size()) {
			CvSet.removeAll(expectedCVs);
			System.err.println(CvSet);
		}
		Assert.assertEquals(expectedCVs.size(), CvSet.size());
		   /* if (CvSet.size() < expectedCVs.size()) {
			   expectedCVs.removeAll(CvSet);
			Set<String> missingSet = new TreeSet<String>();
			Set<String> finalmissingSet = new TreeSet<String>(expectedCVs);
			for(String expectedvalue : expectedCVs) {

			}
			if(finalmissingSet.size() > 0) {
			for(String missingvalue : finalmissingSet) System.err.println(missingvalue);
			String msg = "expression in current solr contains " + finalmissingSet.size() + " more data:";
			System.err.println(msg);
			Assert.fail(msg);
			}
			else Assert.assertTrue(true);
		} */

		// CV_ANCESTORS_ACS
		Set<String> expectedCVancestorsAcs = new TreeSet<String>((List) getValueForFieldInCurrentSolrImplementation(entryName, Fields.CV_ANCESTORS_ACS));
		Set<String> CvancestorsAcsSet = new TreeSet<String>(cfb.getFieldValue(Fields.CV_ANCESTORS_ACS, List.class));
		Assert.assertEquals(expectedCVancestorsAcs.size(), CvancestorsAcsSet.size());
		if (CvancestorsAcsSet.size() > expectedCVancestorsAcs.size()) {
			CvancestorsAcsSet.removeAll(expectedCVancestorsAcs);
			System.err.println(CvancestorsAcsSet);
		}
		else if (CvancestorsAcsSet.size() < expectedCVancestorsAcs.size()) {
			expectedCVancestorsAcs.removeAll(CvancestorsAcsSet);
			System.err.println(expectedCVancestorsAcs);
		}
		
		// CV_ANCESTORS
		Set<String> expectedCVancestors = new TreeSet<String>((List) getValueForFieldInCurrentSolrImplementation(entryName, Fields.CV_ANCESTORS));
		Set<String> CvancestorsSet = new TreeSet<String>(cfb.getFieldValue(Fields.CV_ANCESTORS, List.class));
		Assert.assertEquals(expectedCVancestors.size(), CvancestorsSet.size());
		//System.err.println("CV ancestors OK");
		
		// CV_SYNONYMS
		List<String> synolist = (List) getValueForFieldInCurrentSolrImplementation(entryName, Fields.CV_SYNONYMS);
		if(synolist != null)  {
			Set<String> expectedSynonyms = new TreeSet<String>(synolist);
			Set<String> SynonymsSet = new TreeSet<String>(cfb.getFieldValue(Fields.CV_SYNONYMS, List.class));
			//Assert.assertEquals(expectedSynonyms.size(), SynonymsSet.size());
			Assert.assertEquals(expectedSynonyms, SynonymsSet);
		}
		
		// CV_NAMES
		Set<String> expectedCVNames = new TreeSet<String>((List) getValueForFieldInCurrentSolrImplementation(entryName, Fields.CV_NAMES));
		Set<String> CVNamesSet = new TreeSet<String>(cfb.getFieldValue(Fields.CV_NAMES, List.class));
		Assert.assertEquals(expectedCVNames.size(), CVNamesSet.size());
		//Assert.assertTrue(true);
		if (CVNamesSet.size() < expectedCVNames.size()) {
			expectedCVNames.removeAll(CVNamesSet);
			System.err.println(expectedCVNames);
		}
		
		// EC_NAME -> current solr implementation doesn't index ECs for multifunctional enzymes (eg:NX_P14060) discuss with PAM
		String expected = (String) getValueForFieldInCurrentSolrImplementation(entryName, Fields.EC_NAME);
		if(expected != null)  {
			String ECsString = cfb.getFieldValue(Fields.EC_NAME, String.class);
			Assert.assertEquals(expected, ECsString);
		}
		
	}
}
