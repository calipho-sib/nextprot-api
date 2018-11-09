package org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.integrationtest;

import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.ChromosomeSolrFieldCollector;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.integrationtest.diff.SolrDiffTest.getFieldValue;


public class ChromosomeFieldBuilderIntegrationTest extends SolrBuildIntegrationTest {

	@Autowired	private EntryBuilderService entryBuilderService = null;

	@Test
	public void testChrLoc() {
		
		EntrySolrField field = EntrySolrField.CHR_LOC;
		String entryName = "NX_Q06124";
		
		Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryName).withChromosomalLocations());

		ChromosomeSolrFieldCollector cfb = new ChromosomeSolrFieldCollector();
		Map<EntrySolrField, Object> fields = new HashMap<>();
		cfb.collect(fields, entry, false);
		String chrLocValue = getFieldValue(fields, field, String.class);
		
		
		assertTrue(chrLocValue.contains("12q24.13"));

	}
}
