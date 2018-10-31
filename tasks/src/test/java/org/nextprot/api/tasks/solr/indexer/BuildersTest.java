package org.nextprot.api.tasks.solr.indexer;

import org.junit.Test;
import org.nextprot.api.commons.dbunit.AbstractUnitBaseTest;
import org.nextprot.api.solr.index.EntryField;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Map;

import static org.junit.Assert.fail;

@ActiveProfiles({"build"})
@ContextConfiguration("classpath:spring/commons-context.xml")
public class BuildersTest extends AbstractUnitBaseTest {

	@Test
	public void shouldCoverAllSolrFields() {
		Map<EntryField, EntryFieldBuilder> map = SolrEntryDocumentFactory.mapBuildersByEntryField();
		
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
