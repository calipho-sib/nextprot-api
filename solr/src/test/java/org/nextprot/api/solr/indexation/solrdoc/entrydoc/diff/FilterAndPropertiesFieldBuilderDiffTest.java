package org.nextprot.api.solr.indexation.solrdoc.entrydoc.diff;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.core.EntrySolrField;
import org.nextprot.api.solr.indexation.solrdoc.entrydoc.FilterAndPropertiesFieldsCollector;
import org.nextprot.api.solr.indexation.solrdoc.entrydoc.SolrDiffTest;

import java.util.HashMap;
import java.util.Map;

public class FilterAndPropertiesFieldBuilderDiffTest extends SolrDiffTest {

	@Ignore
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
		FilterAndPropertiesFieldsCollector ffb = new FilterAndPropertiesFieldsCollector();
		Map<EntrySolrField, Object> fields = new HashMap<>();
		ffb.collect(fields, entry, false);
		
		int expectedCount = 0;
		
		expectedCount = (int) getValueForFieldInCurrentSolrImplementation(entryName, EntrySolrField.ISOFORM_NUM);
		Assert.assertEquals(expectedCount, (int) getFieldValue(fields, EntrySolrField.ISOFORM_NUM, Integer.class));
		
		if(getValueForFieldInCurrentSolrImplementation(entryName, EntrySolrField.PTM_NUM) != null) {
		expectedCount = (int) getValueForFieldInCurrentSolrImplementation(entryName, EntrySolrField.PTM_NUM);
		Assert.assertEquals(expectedCount, (int) getFieldValue(fields, EntrySolrField.PTM_NUM, Integer.class));
		}

		expectedCount = (int) getValueForFieldInCurrentSolrImplementation(entryName, EntrySolrField.AA_LENGTH);
		Assert.assertEquals(expectedCount, (int) getFieldValue(fields, EntrySolrField.AA_LENGTH, Integer.class));
		
		String expectedFilters = (String) getValueForFieldInCurrentSolrImplementation(entryName, EntrySolrField.FILTERS);
		Assert.assertEquals(expectedFilters, getFieldValue(fields, EntrySolrField.FILTERS, String.class));
}

}
