package org.nextprot.api.tasks.solr.indexer.entry.integration;

import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.solr.index.EntryField;
import org.nextprot.api.tasks.solr.indexer.entry.SolrBuildIntegrationTest;
import org.nextprot.api.tasks.solr.indexer.entry.impl.ChromosomeFieldBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertTrue;


public class ChromosomeFieldBuilderIntegrationTest extends SolrBuildIntegrationTest{

	@Autowired	private EntryBuilderService entryBuilderService = null;

	@Test
	public void testChrLoc() {
		
		EntryField field = EntryField.CHR_LOC;
		String entryName = "NX_Q06124";
		
		Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryName).withChromosomalLocations());

		ChromosomeFieldBuilder cfb = new ChromosomeFieldBuilder();
		cfb.initializeBuilder(entry);
		String chrLocValue = cfb.getFieldValue(field, String.class);
		
		
		assertTrue(chrLocValue.contains("12q24.13"));

	}
}
