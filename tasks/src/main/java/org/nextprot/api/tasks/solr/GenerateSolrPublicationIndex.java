package org.nextprot.api.tasks.solr;

import java.util.List;

import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.api.tasks.solr.indexer.PublicationSolrindexer;
import org.nextprot.api.tasks.solr.indexer.SolrIndexer;

public class GenerateSolrPublicationIndex extends GenerateSolrIndex {

	
	public static void main(String[] args) {
		GenerateSolrPublicationIndex i = new GenerateSolrPublicationIndex();
		i.launch(args);
	}	    

	@Override
	public void start(String[] args) {

		PublicationService publicationService = getBean(PublicationService.class);
		
		int pubcnt = 0, artcnt = 0, submcnt = 0, othercnt = 0;
		
		String solrServer = System.getProperty("solr.server");
		NPreconditions.checkNotNull(solrServer, "Please set solr.server variable. For example: java -Dsolr.server=http://localhost:8983/solr/nppublications1");
		logger.info("Solr server: " + solrServer); 

		SolrIndexer<Publication> indexer = new PublicationSolrindexer(solrServer);
		
		// Remove previous indexes
		logger.info("removing all solr publication records");
		indexer.clearDatabase("");
		
		List<Long> allpubids;
		
		System.err.println("getting all publications from API");
		logger.info("getting all publications from API");
		long start = System.currentTimeMillis();
		allpubids = publicationService.findAllPublicationIds();
		System.err.println("indexing " + allpubids.size() +  " publications...");
		logger.info("indexing " + allpubids.size() +  " publications...");
		for (Long id : allpubids) {
			//System.err.println("id: " + id);
			Publication currpub = publicationService.findPublicationById(id);
			if(currpub.getPublicationType().equals("ARTICLE"))
			{
			indexer.add(currpub);
			pubcnt++;
			}
			if((pubcnt % 1000)==0) System.err.println(pubcnt + " publications done");
			//if(pubcnt >= 350000) break;
		}
		
		indexer.addRemaing();
		
		logger.info("comitting");
		indexer.commit();
		logger.info(pubcnt + " publications indexed..." + (System.currentTimeMillis()-start)/1000 + " seconds...");
		System.err.println(pubcnt + " publications indexed.");
		System.err.println("All this in " + (System.currentTimeMillis()-start)/1000 + " seconds...");
		
	}
	
}
