package org.nextprot.api.tasks.solr.indexer;

import org.junit.Test;
import org.nextprot.api.commons.dbunit.AbstractUnitBaseTest;
import org.nextprot.api.solr.index.EntryField;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.fail;

public class BuildersTest extends AbstractUnitBaseTest {
	
	@Test
	public void shouldCoverAllSolrFields() {
		Map<EntryField, FieldBuilder> map = new HashMap<>();
		EntrySolrIndexer.initializeFieldBuilders(map);
		
		StringBuilder sb = new StringBuilder();
		for(EntryField f : EntryField.values()){
			if(!f.equals(EntryField.SCORE) && !f.equals(EntryField.TEXT) && !map.containsKey(f)){
				sb.append(f + ",");
			}
		}
		
		if(!sb.toString().isEmpty()){
			fail("Missing " +  sb.toString().split(",").length  + " fields " + " :" + sb.toString());
		}
		
	}


}
