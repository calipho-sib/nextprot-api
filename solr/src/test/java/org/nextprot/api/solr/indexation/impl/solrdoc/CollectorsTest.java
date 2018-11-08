package org.nextprot.api.solr.indexation.impl.solrdoc;

import org.junit.Test;
import org.nextprot.api.commons.dbunit.AbstractUnitBaseTest;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.EntrySolrFieldCollector;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Map;

import static org.junit.Assert.fail;

@ActiveProfiles({"build"})
@ContextConfiguration("classpath:spring/commons-context.xml")
public class CollectorsTest extends AbstractUnitBaseTest {

	@Test
	public void shouldCoverAllSolrFields() {
		Map<EntrySolrField, EntrySolrFieldCollector> map = SolrEntryDocumentFactory.mapCollectorsByEntryField();
		
		StringBuilder sb = new StringBuilder();
		for(EntrySolrField f : EntrySolrField.values()){
			if(!f.equals(EntrySolrField.SCORE) && !f.equals(EntrySolrField.TEXT) && !map.containsKey(f)){
				sb.append(f + ",");
			}
		}
		
		if(!sb.toString().isEmpty()){
			fail("Missing " +  sb.toString().split(",").length  + " fields " + " :" + sb.toString());
		}
	}
}
