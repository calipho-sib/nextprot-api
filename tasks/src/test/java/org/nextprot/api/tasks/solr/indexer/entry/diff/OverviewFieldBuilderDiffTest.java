package org.nextprot.api.tasks.solr.indexer.entry.diff;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.SolrDiffTest;
import org.nextprot.api.tasks.solr.indexer.entry.impl.OverviewFieldBuilder;

public class OverviewFieldBuilderDiffTest extends SolrDiffTest {

	@Test
	public void testOverview() {

		
		 for(int i=0; i < 10; i++){ testOverview(getEntry(i)); }
		 

		//Entry entry = getEntry("NX_Q96I99");
		//testOverview(entry);

	}

	public void testOverview(Entry entry) {
		String entryName = entry.getUniqueName();

		OverviewFieldBuilder ofb = new OverviewFieldBuilder();
		ofb.initializeBuilder(entry);
		
		String expectedRecname = (String) getValueForFieldInCurrentSolrImplementation(entryName, Fields.RECOMMENDED_NAME);
		Assert.assertEquals(ofb.getFieldValue(Fields.RECOMMENDED_NAME, String.class), expectedRecname);
		
		String expectedPE = (String) getValueForFieldInCurrentSolrImplementation(entryName, Fields.PROTEIN_EXISTENCE);
		Assert.assertEquals(ofb.getFieldValue(Fields.PROTEIN_EXISTENCE, String.class), expectedPE.replace(" ", "_"));
	}

}
