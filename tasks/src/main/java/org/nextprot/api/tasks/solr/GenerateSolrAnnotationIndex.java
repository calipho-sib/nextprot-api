package org.nextprot.api.tasks.solr;

import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.*;
import org.nextprot.api.tasks.solr.indexer.EntrySolrIndexer;

import java.util.Set;

@Deprecated
class GenerateSolrAnnotationIndex extends GenerateSolrIndex {

	public static void main(String[] args) {
		GenerateSolrAnnotationIndex i = new GenerateSolrAnnotationIndex();
		i.launch(args);
	}	    

	@Override
	public void start(String[] args) {

		MasterIdentifierService MasterEntryService = getBean(MasterIdentifierService.class);
		EntryBuilderService entryBuilderService = getBean(EntryBuilderService.class);
		
		int ecnt = 0;
		
		String solrServer = System.getProperty("solr.server");
		NPreconditions.checkNotNull(solrServer, "Please set solr.server variable. For example: java -Dsolr.server=\"http://localhost:8983/solr/npentries1\"");
		logger.info("Solr server: " + solrServer); 
		EntrySolrIndexer indexer = new EntrySolrIndexer(solrServer);
		// Get an access to some needed services
		indexer.setTerminologyservice(getBean(TerminologyService.class));
		indexer.setEntryBuilderService(getBean(EntryBuilderService.class));
        indexer.setPublicationService(getBean(PublicationService.class));
        indexer.setEntryReportStatsService(getBean(EntryReportStatsService.class));

		// Remove previous indexes
		logger.info("removing all solr entries records");
		indexer.clearDatabase("");
		
		Set<String> allentryids;
		
		//System.err.println("getting all entries from API");
		logger.info("getting all entries from API");
		long start = System.currentTimeMillis();
		allentryids = MasterEntryService.findUniqueNames();
		logger.info("indexing " + allentryids.size() +  " entries...");

		for (String id : allentryids) {
			ecnt++;
			//String id = "NX_Q8WZ42"; // debug (titin)
			//String id = "NX_Q14524"; // debug (scn5a)
			logger.info(System.currentTimeMillis() + ": start building " + id);
			Entry currentry = entryBuilderService.buildWithEverything(id);
			indexer.convertAndAddDocsToSolr(currentry);
			logger.info(id + " done...");
			if((ecnt % 1000) == 0)
				logger.info(ecnt +  " entries indexed...");
		}
		
		indexer.flushRemainingDocsToSolr();
		
		logger.info("comitting");
		indexer.commit();
		logger.info(ecnt + " entries indexed in " + (System.currentTimeMillis()-start)/1000 + " seconds...END");
	}
	
}
