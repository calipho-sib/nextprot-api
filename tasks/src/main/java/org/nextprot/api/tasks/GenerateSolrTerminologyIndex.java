package org.nextprot.api.tasks;

import java.util.List;

import org.nextprot.api.core.domain.Terminology;
import org.nextprot.api.core.service.TerminologyService;

public class GenerateSolrTerminologyIndex extends GenerateSolrIndex {

	
	public static void main(String[] args) {
		GenerateSolrTerminologyIndex i = new GenerateSolrTerminologyIndex();
		i.launch(args);
	}	    
	
	@Override
	public void start(String[] args) {

		TerminologyService terminologyService = getBean(TerminologyService.class);
		
		int termcnt = 0;
		
		SolrIndexer<Terminology> indexer = new CvTermSolrIndexer("http://localhost:8983/solr/npcvs1");
		
		// Remove previous indexes, TODO: find appropriate string for ontology-specific deletion (filters:ontologyname?)
		logger.info("removing all solr terminology records");
		
		List<Terminology> allterms;
		if (args.length == 0) { // No arg: index all ontologies
			System.err.println("indexing: all ontologies");
			logger.info("indexing all terminologies");
			indexer.clearDatabase("");
			allterms = terminologyService.findAllTerminology();
		} else { // Index ontology given as argument
			System.err.println("indexing: " + args[0]);
			logger.info("indexing terminology: " + args[0]);
			indexer.clearDatabase(args[0]);
			allterms = terminologyService.findTerminologyByOntology(args[0]);
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
