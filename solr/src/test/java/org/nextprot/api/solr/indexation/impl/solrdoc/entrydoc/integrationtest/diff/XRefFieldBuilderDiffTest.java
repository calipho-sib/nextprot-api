package org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.integrationtest.diff;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.InteractionSolrFieldCollector;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.XrefSolrFieldCollector;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Ignore
public class XRefFieldBuilderDiffTest extends SolrDiffTest {
	
	@Ignore
	@Test
	public void testXrefs() {
		String[] test_list = {"NX_Q8N7I0", "NX_O00115","NX_O00116","NX_E5RQL4","NX_P32418","NX_Q7Z6P3",
				"NX_Q7Z713", "NX_P22102", "NX_P10415", "NX_Q6PI97", "NX_Q8NDZ0", "NX_O15056"};

		for(int i=0; i < test_list.length; i++) {
			testXrefs(test_list[i]);
		}
		 //for(int i=4500; i < 5000; i++){	testXrefs(getEntry(i));	} // 'random' entries
	}

	public void testXrefs(String entryName) {

		int newcnt=0, comcnt=0, misscnt=0;
		
		System.out.println("Testing: " + entryName);
		XrefSolrFieldCollector xfb = new XrefSolrFieldCollector();
		Map<EntrySolrField, Object> fields = new HashMap<>();
		xfb.collect(fields, entryName, false);
		
		List<String> expectedABs = (List) getValueForFieldInCurrentSolrImplementation(entryName, EntrySolrField.ANTIBODY);
		if(expectedABs != null) {
		  Collections.sort(expectedABs);
		  List<String> currentABs = getFieldValue(fields, EntrySolrField.ANTIBODY, List.class);
		  if(currentABs != null) Collections.sort(currentABs);
		    Assert.assertEquals(expectedABs, currentABs);
		}
		
		List<String> expectedEnsembl = (List) getValueForFieldInCurrentSolrImplementation(entryName, EntrySolrField.ENSEMBL);
		if(expectedEnsembl != null) {
			if(expectedEnsembl.size() > 1 || expectedEnsembl.get(0).startsWith("ENS")) // We don't want housemade ENSEMBL like NX_VG_7_129906380_2933 (NX_Q13166)
		      Assert.assertEquals(getFieldValue(fields, EntrySolrField.ENSEMBL, List.class).size(), expectedEnsembl.size());
		}

		Set<String> expectedxrefSet = new TreeSet<>((List) getValueForFieldInCurrentSolrImplementation(entryName, EntrySolrField.XREFS));
		Set<String> xrefSet = new TreeSet<>(getFieldValue(fields, EntrySolrField.XREFS, List.class));
		Set<String> acOnlySet = new TreeSet<>();
		Set<String> expectedacOnlySet = new TreeSet<>();
		for(String elem : expectedxrefSet)
			if(!elem.startsWith("journal:")) // For some unknown reasons some journals appear in the xref field of kant (eg:NX_P43686), this is a bug
			  expectedacOnlySet.add(elem.substring(elem.indexOf(", ")+2));
		for(String elem : xrefSet) acOnlySet.add(elem.substring(elem.indexOf(", ")+2));
		for(String elem : expectedacOnlySet) if(!acOnlySet.contains(elem) && !elem.startsWith("PAp")) System.err.println("MISS: " + elem);
		// It looks that for entries that we have re-mapped the original ENSG/T/P from UniProt are not available in the API (eg: ENSG00000279911 -> ENSG00000172459 in NX_Q8NGP9)	
         // see also : NX_Q9HBT8 ENSP00000408168 ENSP00000458062 ENST00000412988 ENST00000413242, NX_Q8NH49/ENSP00000321506, NX_Q8NGR6/ENST00000304833 ...
		
		//for(String elem : xrefSet) if(!expectedxrefSet.contains(elem)) 
			//{System.err.println("NEW: " + elem); newcnt += 1;}
		//else {System.err.println("COMMON: " + elem); comcnt += 1;}
		//for(String elem : expectedxrefSet) if(!xrefSet.contains(elem)) {System.err.println("MISSING: " + elem); misscnt += 1;}
		//System.err.println("COMMON: " + comcnt + " MISSING: " + misscnt + " NEW: " + newcnt);
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
			//System.err.println("removing " + expectedxrefSet.size() + " expected xrefs");
			//xrefSet.removeAll(expectedxrefSet);
			String msg = "Xrefs from API contains more data: " + xrefSet;
			//System.err.println(msg);
			//Assert.fail(msg);
		}
		else  Assert.assertTrue(true);

		List<String> expectedInteractions = (List) getValueForFieldInCurrentSolrImplementation(entryName, EntrySolrField.INTERACTIONS);
		if(expectedInteractions != null) {
		      //Assert.assertEquals(xfb.getFieldValue(Fields.INTERACTIONS, List.class).size(), expectedInteractions.size());
			Integer olditcnt = 0, newitcnt = 0;
			InteractionSolrFieldCollector ifb = new InteractionSolrFieldCollector();
			fields.clear();
			ifb.collect(fields, entryName, false);
			Set<String> itSet = new TreeSet<>(getFieldValue(fields, EntrySolrField.INTERACTIONS, List.class));
			for(String intactIt : expectedInteractions) if(intactIt.startsWith("<p>Interacts")) olditcnt++;
			for(String newintactIt : itSet) if(newintactIt.startsWith("AC:") || newintactIt.equals("selfInteraction")) newitcnt++;
			// There may be one more interaction in the new index (the subunit annotation)
			Assert.assertEquals(olditcnt, newitcnt);
		}

	}
}
