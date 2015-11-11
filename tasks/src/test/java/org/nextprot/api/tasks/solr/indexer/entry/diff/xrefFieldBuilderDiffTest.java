package org.nextprot.api.tasks.solr.indexer.entry.diff;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.SolrDiffTest;
import org.nextprot.api.tasks.solr.indexer.entry.impl.XrefFieldBuilder;

public class xrefFieldBuilderDiffTest extends SolrDiffTest {

	@Test
	public void testXrefs() {

		//for(int i=0; i < 10; i++){ testXrefs(getEntry(i)); } 
		
		Entry entry = getEntry("NX_P20592");
		testXrefs(entry);
	
	}

	
	public void testXrefs(Entry entry) {
		
		String entryName = entry.getUniqueName();

		XrefFieldBuilder xfb = new XrefFieldBuilder();
		xfb.initializeBuilder(entry);
		
		// Xrefs to HPA antibodies temporarily not in the API
		/*List<String> expectedABs = (List) getValueForFieldInCurrentSolrImplementation(entryName, Fields.ANTIBODY);
		if(expectedABs != null) {
		  Collections.sort(expectedABs);
		  List<String> currentABs = xfb.getFieldValue(Fields.ANTIBODY, List.class);
		  Collections.sort(currentABs);
		  Assert.assertEquals(expectedABs, currentABs);
		}*/
		

		List<String> expectedEnsembl = (List) getValueForFieldInCurrentSolrImplementation(entryName, Fields.ENSEMBL);
		if(expectedEnsembl != null)
		  Assert.assertEquals(xfb.getFieldValue(Fields.ENSEMBL, List.class).size(), expectedEnsembl.size());

		List<String> expectedXrefs = (List) getValueForFieldInCurrentSolrImplementation(entryName, Fields.XREFS);
		Set<String> rawxrefSet = new TreeSet<String>(expectedXrefs);
		Set<String> xrefSet = new TreeSet<String>(xfb.getFieldValue(Fields.XREFS, List.class));
		//for(String elem : xrefSet) if(elem.contains("HPAxxx")) System.err.println(elem);
		//System.err.println(expectedXrefs.size() + " -> " + xrefSet.size());
		//System.err.println("xrefSet: " + xrefSet);
		if (xrefSet.size() < rawxrefSet.size()) {
			// Several issues there:
			// 1) missing pubmeds and DOIs -> the ones comming from additional refs (they will be added to entry publications)
			// 2) Refseq nucleotides (XM_, NM_) are labeled as nucleotide sequence ID
			// 3) Domain names are not xrefs eg: entry name:GED
			rawxrefSet.removeAll(xrefSet);
			String msg = "Xrefs in current solr contains more data: " + rawxrefSet;
			System.err.println(msg);
			Assert.fail(msg);
		}
		else if (xrefSet.size() > rawxrefSet.size()) {
			xrefSet.removeAll(rawxrefSet);
			String msg = "Xrefs from API contains more data: " + xrefSet;
			System.err.println(msg);
			Assert.fail(msg);
		}
		else  Assert.assertTrue(true);
	}
}
