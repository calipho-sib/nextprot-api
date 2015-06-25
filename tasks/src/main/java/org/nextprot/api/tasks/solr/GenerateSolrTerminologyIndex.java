package org.nextprot.api.tasks.solr;

import java.util.List;

import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.core.domain.Terminology;
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
		SolrIndexer<Terminology> indexer = new CvTermSolrIndexer(solrServer);
		//logger.info("removing all solr terminology records");
		
		List<Terminology> allterms;
		if (ontologyToReindex == null) { // No arg: index all ontologies
			System.err.println("indexing: all ontologies");
			logger.info("indexing all terminologies");
			indexer.clearDatabase("");
			allterms = terminologyService.findAllTerminology();
		} else { // Index ontology given as VM argument
			System.err.println("indexing: " + ontologyToReindex);
			logger.info("indexing terminology: " + ontologyToReindex);
			indexer.clearDatabase("filters:" + ontologyToReindex);
			allterms = terminologyService.findTerminologyByOntology(ontologyToReindex);
		}

		for (Terminology t : allterms) {
			indexer.add(t);
			termcnt++;
		}

		indexer.addRemaing();
		
		logger.info("comitting");
		indexer.commit();
		logger.info(termcnt + " terms indexed...");
		System.err.println(termcnt + " terms indexed...");
	}
	
}
