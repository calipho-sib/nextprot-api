package org.nextprot.api.tasks.solr;

import java.util.List;

import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.tasks.solr.indexer.CvTermSolrIndexer;
import org.nextprot.api.tasks.solr.indexer.SolrIndexer;

public class GenerateSolrTerminologyIndex extends GenerateSolrIndex {

	
	public static void main(String[] args) {
		GenerateSolrTerminologyIndex i = new GenerateSolrTerminologyIndex();
		i.launch(args);
	}	    
	
	@Override
	public void start(String[] args) {

		TerminologyService terminologyService = getBean(TerminologyService.class);
		
		int termcnt = 0;
		
		String solrServer = System.getProperty("solr.server");
		NPreconditions.checkNotNull(solrServer, "Please set solr.server variable. For example: java -Dsolr.server=\"http://localhost:8983/solr/npcvs1\"");
		logger.info("Solr server: " + solrServer); 
		
		String ontologyToReindex = System.getProperty("solr.ontology"); // eg: java -Dsolr.ontology="UniprotFamilyCv" (don't forget CamelCasing)
		SolrIndexer<CvTerm> indexer = new CvTermSolrIndexer(solrServer);
		
		List<CvTerm> allterms;
		if (ontologyToReindex == null) { // No arg: index all ontologies
			System.err.println("indexing: all ontologies");
			logger.info("indexing all terminologies");
			indexer.clearDatabase("");
			allterms = terminologyService.findAllCVTerms();
		} else { // Index ontology given as VM argument
			System.err.println("indexing: " + ontologyToReindex);
			logger.info("indexing terminology: " + ontologyToReindex);
			indexer.clearDatabase("filters:" + ontologyToReindex);
			allterms = terminologyService.findCvTermsByOntology(ontologyToReindex);
		}

		for (CvTerm term : allterms) {
			indexer.add(term);
			termcnt++;
			if((termcnt % 3000)==0)
				logger.info(termcnt + "/" + allterms.size() + " cv terms done");
		}

		indexer.flushRemainingDocsToSolr();
		
		logger.info("comitting");
		indexer.commit();
		logger.info(termcnt + " terms indexed...END");
	}
	
}
