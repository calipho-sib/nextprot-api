package org.nextprot.api.tasks.solr.indexer;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;

public class BuildersTest extends CoreUnitBaseTest {
	
	@Test
	public void shouldCoverAllSolrFields() {
		Map<Fields, FieldBuilder> map = new HashMap<>();
		EntrySolrIndexer.initializeFieldBuilders(map);
		
		StringBuilder sb = new StringBuilder();
		for(Fields f : Fields.values()){
			if(!f.equals(Fields.SCORE) && !f.equals(Fields.TEXT) && !map.containsKey(f)){
				sb.append(f + ",");
			}
		}
		
		if(!sb.toString().isEmpty()){
			fail("Missing " +  sb.toString().split(",").length  + " fields " + " :" + sb.toString());
		}
		
	}


}
