package org.nextprot.api.tasks.solr.indexer.entry.diff;

import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.tasks.solr.indexer.entry.SolrDiffTest;

public class OverviewFieldBuilderDiffTest extends SolrDiffTest {

	@Test
	public void testOverview() {

		/*
		 * for(int i=0; i < 10; i++){ testPeptides(getEntry(i)); }
		 */

		Entry entry = getEntry("NX_Q96I99");
		testOverview(entry);

	}

	public void testOverview(Entry entry) {

	}

}
