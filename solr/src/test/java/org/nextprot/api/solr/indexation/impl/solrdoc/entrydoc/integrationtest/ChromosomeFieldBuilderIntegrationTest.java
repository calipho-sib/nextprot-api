package org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.integrationtest;

import org.junit.Test;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.ChromosomeSolrFieldCollector;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.integrationtest.diff.SolrDiffTest.getFieldValue;


public class ChromosomeFieldBuilderIntegrationTest extends SolrBuildIntegrationTest {

	@Test
	public void testChrLoc() {
		
		EntrySolrField field = EntrySolrField.CHR_LOC;
		String entryName = "NX_Q06124";
		
		ChromosomeSolrFieldCollector cfb = new ChromosomeSolrFieldCollector();
		Map<EntrySolrField, Object> fields = new HashMap<>();
		cfb.collect(fields, entryName, false);
		String chrLocValue = getFieldValue(fields, field, String.class);

		assertTrue(chrLocValue.contains("12q24.13"));
	}
}
