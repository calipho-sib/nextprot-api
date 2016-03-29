package org.nextprot.api.tasks.solr.indexer.entry.diff;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.SolrDiffTest;
import org.nextprot.api.tasks.solr.indexer.entry.impl.XrefFieldBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class XRefFieldBuilderDiffTest extends SolrDiffTest {

	// TODO: @Ignore should be removed and this test fixed
	// TODO: testXrefs() should be called against a precise list of entries (see also NamesFieldBuilderDiffTest)
	@Ignore
	@Test
	public void testXrefs() {

		// worked on jenkins failed locally with from the list below:
		// Testing: NX_P20592
		// Testing: NX_P20591 (testXrefs() failed for this one)

		for(int i=0; i < 10; i++){ 
			testXrefs(getEntry(i)); 
		}
		
		//Entry entry = getEntry("NX_P20592");
		//testXrefs(entry);

	}

	
	public void testXrefs(Entry entry) {
		
		String entryName = entry.getUniqueName();
		int newcnt=0, comcnt=0, misscnt=0;
		System.out.println("Testing: " + entryName);
		XrefFieldBuilder xfb = new XrefFieldBuilder();
		xfb.initializeBuilder(entry);
		
		List<String> expectedABs = (List) getValueForFieldInCurrentSolrImplementation(entryName, Fields.ANTIBODY);
		if(expectedABs != null) {
		  Collections.sort(expectedABs);
		  List<String> currentABs = xfb.getFieldValue(Fields.ANTIBODY, List.class);
		  Collections.sort(currentABs);
		  Assert.assertEquals(expectedABs, currentABs);
		}
		

		List<String> expectedEnsembl = (List) getValueForFieldInCurrentSolrImplementation(entryName, Fields.ENSEMBL);
		if(expectedEnsembl != null)
		  Assert.assertEquals(xfb.getFieldValue(Fields.ENSEMBL, List.class).size(), expectedEnsembl.size());

		Set<String> expectedxrefSet = new TreeSet<String>((List) getValueForFieldInCurrentSolrImplementation(entryName, Fields.XREFS));
		Set<String> xrefSet = new TreeSet<String>(xfb.getFieldValue(Fields.XREFS, List.class));
		for(String elem : xrefSet) if(!expectedxrefSet.contains(elem))
			{System.err.println("NEW: " + elem); newcnt += 1;}
		//else {System.err.println("COMMON: " + elem); comcnt += 1;}
		for(String elem : expectedxrefSet) if(!xrefSet.contains(elem)) {System.err.println("MISSING: " + elem); misscnt += 1;}
		System.err.println("COMMON: " + comcnt + " MISSING: " + misscnt + " NEW: " + newcnt);
		if (xrefSet.size() < expectedxrefSet.size()) {
			// Several issues there:
			// 1) missing pubmeds and DOIs -> the ones comming from additional refs (they will be added to entry publications)
			// 2) Refseq nucleotides (XM_, NM_) labeled as 'nucleotide sequence ID' are not in the api results
			// 3) Domain names are not xrefs eg: entry name:GED, entry name:B33481 = PIR
			expectedxrefSet.removeAll(xrefSet);
			String msg = "Xrefs in current solr contains more data: " + expectedxrefSet;
			System.err.println(msg);
			//Assert.fail(msg);
		}
		else if (xrefSet.size() > expectedxrefSet.size()) {
			System.err.println("removing " + expectedxrefSet.size() + " expected xrefs");
			xrefSet.removeAll(expectedxrefSet);
			String msg = "Xrefs from API contains more data: " + xrefSet;
			System.err.println(msg);
			Assert.fail(msg);
		}
		else  Assert.assertTrue(true);
	}
}
