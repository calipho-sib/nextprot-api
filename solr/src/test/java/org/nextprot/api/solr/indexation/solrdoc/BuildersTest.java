package org.nextprot.api.solr.indexation.solrdoc;

import org.junit.Test;
import org.nextprot.api.commons.dbunit.AbstractUnitBaseTest;
import org.nextprot.api.solr.core.EntrySolrField;
import org.nextprot.api.solr.indexation.solrdoc.entrydoc.EntrySolrFieldCollector;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Map;

import static org.junit.Assert.fail;

@ActiveProfiles({"build"})
@ContextConfiguration("classpath:spring/solr-context.xml")
public class BuildersTest extends AbstractUnitBaseTest {

	@Test
	public void shouldCoverAllSolrFields() {
		Map<EntrySolrField, EntrySolrFieldCollector> map = SolrEntryDocumentFactory.mapBuildersByEntryField();
		
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
