package org.nextprot.api.tasks.solr.indexer.entry.diff;

import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.tasks.solr.indexer.entry.SolrDiffTest;

public class IdentifierFieldBuilderDiffTest extends SolrDiffTest {

	@Test
	public void testIdentifiers() {

		/*for(int i=0; i < 10; i++){
			testPeptides(getEntry(i));
		}*/
		
		Entry entry = getEntry("NX_Q96I99");
		testIdentfiers(entry);
	
	}

	
	public void testIdentfiers(Entry entry) {
		
		//TODO ............
	}
	
}
