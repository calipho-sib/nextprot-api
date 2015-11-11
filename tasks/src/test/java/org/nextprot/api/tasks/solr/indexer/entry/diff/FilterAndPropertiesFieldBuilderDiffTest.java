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

		
		 for(int i=0; i < 10; i++){ testFilterAndProperties(getEntry(i)); }
		 

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
