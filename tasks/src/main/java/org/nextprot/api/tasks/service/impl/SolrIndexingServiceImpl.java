package org.nextprot.api.tasks.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.solr.SolrConfiguration;
import org.nextprot.api.solr.SolrConnectionFactory;
import org.nextprot.api.solr.index.CvIndex;
import org.nextprot.api.tasks.service.SolrIndexingService;
import org.nextprot.api.tasks.solr.indexer.CvTermSolrIndexer;
import org.nextprot.api.tasks.solr.indexer.SolrIndexer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class SolrIndexingServiceImpl implements SolrIndexingService {
	
	protected Logger logger = Logger.getLogger(SolrIndexingServiceImpl.class);

	@Autowired private SolrConnectionFactory connFactory;
	@Autowired private SolrConfiguration configuration;
	@Autowired private TerminologyService terminologyService;
	
	@Override
	public String indexTerminologies() {
		
		long seconds = System.currentTimeMillis() / 1000;
		StringBuilder info = new StringBuilder();
		
		logAndCollect(info, "terms indexing...STARTING at " + new Date());

		String baseUrl = connFactory.getSolrBaseUrl();
		String indexUrl = configuration.getIndexByName(CvIndex.NAME).getUrl();
		String serverUrl = baseUrl + indexUrl;
		logAndCollect(info,"Solr server: " + serverUrl); 		

		logAndCollect(info,"clearing term index");
		SolrIndexer<CvTerm> indexer = new CvTermSolrIndexer(serverUrl);
		List<CvTerm> allterms;
		indexer.clearDatabase("");

		logAndCollect(info,"getting terms for all terminologies");
		allterms = terminologyService.findAllCVTerms();

		logAndCollect(info,"start indexing of " + allterms.size() + " terms");
		int termcnt = 0;
		for (CvTerm term : allterms) {
			indexer.add(term);
			termcnt++;
			if((termcnt % 3000)==0)
				logAndCollect(info,termcnt + "/" + allterms.size() + " cv terms done");
		}
		indexer.addRemaing();
		
		logAndCollect(info,"comitting");
		indexer.commit();
		seconds = (System.currentTimeMillis()/1000 - seconds);
		logAndCollect(info,termcnt + " terms indexed in " + seconds + " seconds ...END at " + new Date());
		
		return info.toString();

	}

	@Override
	public String indexPublications() {
		return null;

	}

	private void logAndCollect(StringBuilder info,String message) {
		logger.info(message);
		info.append(message).append("\n");
	}
	
}
