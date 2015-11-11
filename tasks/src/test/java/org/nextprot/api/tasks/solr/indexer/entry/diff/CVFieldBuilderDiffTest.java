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

		for(int i=0; i < 10; i++){ 	testCVs(getEntry(i)); } 
		
		//Entry entry = getEntry("NX_P20592");
		//Entry entry = getEntry("NX_P14060");
		//testCVs(entry);
	
	}

	
	public void testCVs(Entry entry) {
		
		String entryName = entry.getUniqueName();

		System.out.println("Testing: " + entryName);
		CVFieldBuilder cfb = new CVFieldBuilder();
		cfb.setTerminologyservice(terminologyService);
		cfb.initializeBuilder(entry);
		
		// CV_ACS
		Set<String> expectedCVs = new TreeSet<String>((List) getValueForFieldInCurrentSolrImplementation(entryName, Fields.CV_ACS));
		Set<String> CvSet = new TreeSet<String>(cfb.getFieldValue(Fields.CV_ACS, List.class));
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
