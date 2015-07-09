package org.nextprot.api.tasks.solr;

//import java.util.List;
import java.util.Set;

import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.DbXrefService;
import org.nextprot.api.core.service.EntryService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.tasks.solr.indexer.AnnotationSolrIndexer;

public class GenerateSolrAnnotationIndex extends GenerateSolrIndex {

	
	public static void main(String[] args) {
		GenerateSolrAnnotationIndex i = new GenerateSolrAnnotationIndex();
		i.launch(args);
	}	    

	@Override
	public void start(String[] args) {

		MasterIdentifierService MasterEntryService = getBean(MasterIdentifierService.class);
		EntryService entryService = getBean(EntryService.class);
		
		int ecnt = 0;
		
		String solrServer = System.getProperty("solr.server");
		NPreconditions.checkNotNull(solrServer, "Please set solr.server variable. For example: java -Dsolr.server=\"http://localhost:8983/solr/npentries1\"");
		logger.info("Solr server: " + solrServer); 

		AnnotationSolrIndexer indexer = new AnnotationSolrIndexer(solrServer);
		indexer.setTerminologyservice(getBean(TerminologyService.class));
		indexer.setDbxrefservice(getBean(DbXrefService.class));
		
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
			//System.err.println("id: " + id);
			//Entry currentry = entryService.findEntry("NX_Q86SQ0");
			//Entry currentry = entryService.findEntry("NX_Q08426");
			Entry currentry = entryService.findEntry(id);
			indexer.add(currentry);
			ecnt++;
			if((ecnt % 100) == 0) System.err.println(ecnt + "...");
			if(ecnt >= 100) break;
		}
		
		indexer.addRemaing();
		
		logger.info("comitting");
		indexer.commit();
		logger.info(ecnt + " entries indexed..." + (System.currentTimeMillis()-start)/1000 + " seconds...");
		System.err.println(ecnt + " entries indexed." );
		System.err.println("All this in " + (System.currentTimeMillis()-start)/1000 + " seconds...");
		
	}
	
}
