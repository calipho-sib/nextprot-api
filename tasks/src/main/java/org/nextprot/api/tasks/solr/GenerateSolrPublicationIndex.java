package org.nextprot.api.tasks.solr;

import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.publication.PublicationType;
import org.nextprot.api.core.service.GlobalPublicationService;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.api.tasks.solr.indexer.PublicationSolrindexer;
import org.nextprot.api.tasks.solr.indexer.SolrIndexer;

import java.util.Set;

public class GenerateSolrPublicationIndex extends GenerateSolrIndex {

	public static void main(String[] args) {
		GenerateSolrPublicationIndex i = new GenerateSolrPublicationIndex();
		i.launch(args);
	}	    

	@Override
	public void start(String[] args) {

		PublicationService publicationService = getBean(PublicationService.class);
		GlobalPublicationService globalPublicationService = getBean(GlobalPublicationService.class);

		int pubcnt = 0;
		
		String solrServer = System.getProperty("solr.server");
		NPreconditions.checkNotNull(solrServer, "Please set solr.server variable. For example: java -Dsolr.server=http://localhost:8983/solr/nppublications1");
		logger.info("Solr server: " + solrServer); 

		SolrIndexer<Publication> indexer = new PublicationSolrindexer(solrServer, publicationService);
		
		// Remove previous indexes
		logger.info("removing all solr publication records");
		indexer.clearDatabase("");
		
		logger.info("getting all publications from API");
		long start = System.currentTimeMillis();
        Set<Long> allpubids = globalPublicationService.findAllPublicationIds();
		logger.info("indexing " + allpubids.size() +  " publications...");
		for (Long id : allpubids) {
			Publication currpub = publicationService.findPublicationById(id);
			if(currpub.getPublicationType().equals(PublicationType.ARTICLE)) {
			  indexer.add(currpub);
			  pubcnt++;
			  }
			if((pubcnt % 5000)==0)
				logger.info(pubcnt + "/" + allpubids.size() + " publications done");
		}
		
		indexer.flushRemainingDocsToSolr();
		
		logger.info("comitting");
		indexer.commit();
		logger.info(pubcnt + " publications indexed in " + (System.currentTimeMillis()-start)/1000 + " seconds...END");
	}
	
}
