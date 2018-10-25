package org.nextprot.api.tasks.solr;

import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.tasks.solr.indexer.EntryGoldOnlySolrIndexer;

import java.util.Set;

@Deprecated
class GenerateSolrAnnotationIndexGold extends GenerateSolrIndex {

	
	public static void main(String[] args) {
		GenerateSolrAnnotationIndexGold i = new GenerateSolrAnnotationIndexGold();
		i.launch(args);
	}	    

	@Override
	public void start(String[] args) {

		MasterIdentifierService MasterEntryService = getBean(MasterIdentifierService.class);
		EntryBuilderService entryBuilderService = getBean(EntryBuilderService.class);
		
		int ecnt = 0;
		
		String solrServer = System.getProperty("solr.server");
		NPreconditions.checkNotNull(solrServer, "Please set solr.server variable. For example: java -Dsolr.server=\"http://localhost:8983/solr/npentries1gold\"");
		logger.info("Solr server: " + solrServer); 
		EntryGoldOnlySolrIndexer indexer = new EntryGoldOnlySolrIndexer(solrServer);
		// Get an access to some needed services
		indexer.setTerminologyservice(getBean(TerminologyService.class));
		indexer.setEntryBuilderService(getBean(EntryBuilderService.class));

		// Remove previous indexes
		logger.info("removing all solr entries records");
		indexer.clearDatabase("");
		
		Set<String> allentryids;
		
		System.err.println("getting all entries from API");
		logger.info("getting all entries from API");
		long start = System.currentTimeMillis();
		allentryids = MasterEntryService.findUniqueNames();
		System.err.println("indexing " + allentryids.size() +  " entries...");
		logger.info("indexing " + allentryids.size() +  " entries...");

		for (String id : allentryids) {
			ecnt++;
			Entry currentry = entryBuilderService.buildWithEverything(id);
			indexer.convertAndAddDocsToSolr(currentry);
			if((ecnt % 1000) == 0)
				logger.info(ecnt +  " entries GOLD-indexed...");
		}
		
		indexer.flushRemainingDocsToSolr();
		
		logger.info("comitting");
		indexer.commit();
		logger.info(ecnt + " entries GOLD-indexed in " + (System.currentTimeMillis()-start)/1000 + " seconds...END");
	}
	
}
