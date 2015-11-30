package org.nextprot.api.tasks.solr.indexer.entry.integration;

import org.junit.Test;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.tasks.solr.indexer.entry.SolrBuildIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;


public class AnnotationFieldBuilderIntegrationTest extends SolrBuildIntegrationTest{

	@Autowired	private EntryBuilderService entryBuilderService = null;
	@Autowired	private MasterIdentifierService masterIdentifierService = null;

	@Test
	public void testFunctionalDesc() {
		
	}
}
