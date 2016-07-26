package org.nextprot.api.tasks.solr.indexer.entry.diff;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.SolrDiffTest;
import org.nextprot.api.tasks.solr.indexer.entry.impl.OverviewFieldBuilder;

public class OverviewFieldBuilderDiffTest extends SolrDiffTest {

	@Test
	public void testOverview() {

		String[] test_list = {"NX_Q8IWA4", "NX_O00115","NX_Q7Z6P3","NX_E5RQL4","NX_Q12809","NX_Q7Z6P3",
				"NX_Q7Z713", "NX_P22102", "NX_Q8IYV9", "NX_O00116", "NX_Q7Z713", "NX_O15056"};
		
		 for(int i=0; i < test_list.length; i++){ testOverview(getEntry(test_list[i])); } 
		 //for(int i=0; i < 10; i++){ testOverview(getEntry(i)); } // 'random' entries
		//Entry entry = getEntry("NX_Q96I99");
		//testOverview(entry);

	}

	public void testOverview(Entry entry) {
		String entryName = entry.getUniqueName();
		System.out.println("Testing " + entryName);
		OverviewFieldBuilder ofb = new OverviewFieldBuilder();
		ofb.initializeBuilder(entry);
		
		String expectedRecname = (String) getValueForFieldInCurrentSolrImplementation(entryName, Fields.RECOMMENDED_NAME);
		Assert.assertEquals(ofb.getFieldValue(Fields.RECOMMENDED_NAME, String.class), expectedRecname);
		
		String expectedPE = (String) getValueForFieldInCurrentSolrImplementation(entryName, Fields.PROTEIN_EXISTENCE);
		Assert.assertEquals(ofb.getFieldValue(Fields.PROTEIN_EXISTENCE, String.class), expectedPE.replace(" ", "_"));
	}

}
