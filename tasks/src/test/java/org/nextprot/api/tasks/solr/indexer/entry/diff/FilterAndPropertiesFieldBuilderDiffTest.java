package org.nextprot.api.tasks.solr.indexer.entry.diff;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.SolrDiffTest;
import org.nextprot.api.tasks.solr.indexer.entry.impl.FilterAndPropertiesFieldsBuilder;

public class FilterAndPropertiesFieldBuilderDiffTest extends SolrDiffTest {

	@Test
	public void testFilterAndProperties() {

		String[] test_list = {"NX_Q8IWA4", "NX_O00115","NX_Q7Z6P3","NX_E5RQL4","NX_Q12809","NX_Q7Z6P3",
				"NX_Q7Z713", "NX_P22102", "NX_Q8IYV9", "NX_O00116", "NX_Q7Z713", "NX_O15056"};

		 for(int i=0; i < test_list.length; i++){ testFilterAndProperties(getEntry(test_list[i])); }
		 //for(int i=0; i < 10; i++){ testFilterAndProperties(getEntry(i)); } // 'random' entries
		 
		//Entry entry = getEntry("NX_Q96I99");
		//testFilterAndProperties(entry);
	}

	public void testFilterAndProperties(Entry entry) {
		String entryName = entry.getUniqueName();
		System.out.println("Testing " + entryName);
		FilterAndPropertiesFieldsBuilder ffb = new FilterAndPropertiesFieldsBuilder();
		ffb.initializeBuilder(entry);
		
		int expectedCount = 0;
		
		expectedCount = (int) getValueForFieldInCurrentSolrImplementation(entryName, Fields.ISOFORM_NUM);
		Assert.assertEquals(expectedCount, (int) ffb.getFieldValue(Fields.ISOFORM_NUM, Integer.class));
		
		if(getValueForFieldInCurrentSolrImplementation(entryName, Fields.PTM_NUM) != null) {
		expectedCount = (int) getValueForFieldInCurrentSolrImplementation(entryName, Fields.PTM_NUM);
		Assert.assertEquals(expectedCount, (int) ffb.getFieldValue(Fields.PTM_NUM, Integer.class));
		}

		expectedCount = (int) getValueForFieldInCurrentSolrImplementation(entryName, Fields.AA_LENGTH);
		Assert.assertEquals(expectedCount, (int) ffb.getFieldValue(Fields.AA_LENGTH, Integer.class));
		
		String expectedFilters = (String) getValueForFieldInCurrentSolrImplementation(entryName, Fields.FILTERS);
		Assert.assertEquals(expectedFilters, ffb.getFieldValue(Fields.FILTERS, String.class));
}

}
