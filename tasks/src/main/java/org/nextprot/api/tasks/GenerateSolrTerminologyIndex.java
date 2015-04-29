package org.nextprot.api.tasks;

import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.nextprot.api.core.domain.Terminology;
import org.nextprot.api.core.service.TerminologyService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class GenerateSolrTerminologyIndex {

	public static void main(String[] args) throws SolrServerException, IOException {

		System.setProperty("spring.profiles.active", "dev");
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:spring/commons-context.xml",	"classpath:spring/core-context.xml");
		TerminologyService terminologyService = ctx.getBean(TerminologyService.class);
		
		int termcnt = 0;
		
		SolrIndexer<Terminology> indexer = new CvTermSolrIndexer("http://localhost:8983/solr/npcvs1");
		
		// Remove previous indexes, find appropriate string for ontology-specific deletion (filters:ontologyname?)
		indexer.deleteByQuery( "*:*" );
		indexer.commit();
		
		List<Terminology> allterms;
		if(args.length == 0) { // No arg: index all ontologies
			System.err.println("indexing: all ontologies");
			allterms = terminologyService.findAllTerminology();
		   }
		else { // Index ontology given as argument
			System.err.println("indexing: " + args[0]);
			allterms = terminologyService.findTerminologyByOntology(args[0]);
		   }
		for (Terminology t : allterms) {
			indexer.add(t);
			termcnt++;
			//if((args.length == 0) && (termcnt >= 1000)) break;
			}
		
		if(indexer.docs.size() > 0) // There are some prepared docs not yet sent to solr server
			indexer.solrServer.add(indexer.docs);
		
		indexer.commit();
		System.err.println(termcnt + " terms indexed...");
	}
	
}
