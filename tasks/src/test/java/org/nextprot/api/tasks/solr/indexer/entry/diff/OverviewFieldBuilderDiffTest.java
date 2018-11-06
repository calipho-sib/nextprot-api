package org.nextprot.api.tasks.solr.indexer.entry.diff;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.core.EntrySolrField;
import org.nextprot.api.tasks.solr.indexer.entry.SolrDiffTest;
import org.nextprot.api.tasks.solr.indexer.entry.impl.OverviewSolrFieldCollector;

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
		OverviewSolrFieldCollector ofb = new OverviewSolrFieldCollector();
		ofb.collect(entry, false);
		
		String expectedRecname = (String) getValueForFieldInCurrentSolrImplementation(entryName, EntrySolrField.RECOMMENDED_NAME);
		Assert.assertEquals(ofb.getFieldValue(EntrySolrField.RECOMMENDED_NAME, String.class), expectedRecname);
		
		String expectedPE = (String) getValueForFieldInCurrentSolrImplementation(entryName, EntrySolrField.PROTEIN_EXISTENCE);
		Assert.assertEquals(ofb.getFieldValue(EntrySolrField.PROTEIN_EXISTENCE, String.class), expectedPE.replace(" ", "_"));
	}

}
