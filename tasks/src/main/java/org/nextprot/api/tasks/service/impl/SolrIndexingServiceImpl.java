package org.nextprot.api.tasks.service.impl;

import org.apache.log4j.Logger;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.publication.PublicationType;
import org.nextprot.api.core.service.*;
import org.nextprot.api.solr.SolrConfiguration;
import org.nextprot.api.solr.SolrConnectionFactory;
import org.nextprot.api.solr.index.CvIndex;
import org.nextprot.api.solr.index.GoldAndSilverEntryIndex;
import org.nextprot.api.solr.index.GoldOnlyEntryIndex;
import org.nextprot.api.solr.index.PublicationIndex;
import org.nextprot.api.tasks.service.SolrIndexingService;
import org.nextprot.api.tasks.solr.indexer.CvTermSolrIndexer;
import org.nextprot.api.tasks.solr.indexer.EntrySolrIndexer;
import org.nextprot.api.tasks.solr.indexer.PublicationSolrIndexer;
import org.nextprot.api.tasks.solr.indexer.SolrIndexer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Lazy
@Service
public class SolrIndexingServiceImpl implements SolrIndexingService {
	
	protected Logger logger = Logger.getLogger(SolrIndexingServiceImpl.class);

	@Autowired private SolrConnectionFactory connFactory;
	@Autowired private SolrConfiguration configuration;
	@Autowired private TerminologyService terminologyService;
	@Autowired private PublicationService publicationService;
	@Autowired private GlobalPublicationService globalPublicationService;
	@Autowired private EntryBuilderService entryBuilderService ;
	@Autowired private MasterIdentifierService masterIdentifierService;
	@Autowired private EntryReportStatsService entryReportStatsService;

	@Override
	public String indexEntriesChromosome(boolean isGold, String chrName) {

		long seconds = System.currentTimeMillis() / 1000;
		StringBuilder info = new StringBuilder();
		
		String indexName = isGold ?  GoldOnlyEntryIndex.NAME : GoldAndSilverEntryIndex.NAME;
		logAndCollect(info, "adding entries to index " + indexName + " from chromosome " + chrName + "...STARTING at " + new Date());

		EntrySolrIndexer indexer = newSolrIndexer(indexName, isGold, info);
		indexer.setTerminologyservice(terminologyService);
		indexer.setEntryBuilderService(entryBuilderService);
		indexer.setPublicationService(publicationService);
		indexer.setEntryReportStatsService(entryReportStatsService);

		logAndCollect(info,"getting entry list of chromosome " + chrName);
		List<String> allentryids = masterIdentifierService.findUniqueNamesOfChromosome(chrName);

		logAndCollect(info,"start indexing of " + allentryids.size() + " entries");
		int ecnt=0;
		for (String id : allentryids) {
			ecnt++;
			indexer.convertAndAddDocsToSolr(entryBuilderService.buildWithEverything(id));
			if((ecnt % 300) == 0)
				logAndCollect(info,ecnt + "/" + allentryids.size() + " entries added to index " + indexName + " for chromosome " + chrName);
		}
		indexer.flushRemainingDocsToSolr();
		
		logAndCollect(info,"committing index " + indexName);
		indexer.commit();
		
		seconds = (System.currentTimeMillis()/1000 - seconds);
		logAndCollect(info,"added entries to index " + indexName + "from chromosome " + chrName + " in " + seconds + " seconds ...END at " + new Date());

		return info.toString();
	}

	@Override
	public String initIndexEntries(boolean isGold) {
		
		long seconds = System.currentTimeMillis() / 1000;
		StringBuilder info = new StringBuilder();
		
		String indexName = isGold ?  GoldOnlyEntryIndex.NAME : GoldAndSilverEntryIndex.NAME;
		logAndCollect(info, "initializing index " + indexName + "...STARTING at " + new Date());

		logAndCollect(info,"clearing index " + indexName);
		SolrIndexer indexer = newSolrIndexer(indexName, isGold, info);
		indexer.clearDatabase("");
		
		logAndCollect(info,"committing index " + indexName);
		indexer.commit();

		seconds = (System.currentTimeMillis()/1000 - seconds);
		logAndCollect(info,"index " + indexName + " initialized in " + seconds + " seconds ...END at " + new Date());

		return info.toString();
	}
	
	@Override
	public String indexTerminologies() {
		
		long seconds = System.currentTimeMillis() / 1000;
		StringBuilder info = new StringBuilder();
		logAndCollect(info, "terms indexing...STARTING at " + new Date());

		String serverUrl = getServerUrl(CvIndex.NAME);
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
			indexer.convertAndAddDocsToSolr(term);
			termcnt++;
			if((termcnt % 3000)==0)
				logAndCollect(info,termcnt + "/" + allterms.size() + " cv terms done");
		}
		indexer.flushRemainingDocsToSolr();
		
		logAndCollect(info,"committing");
		indexer.commit();
		seconds = (System.currentTimeMillis()/1000 - seconds);
		logAndCollect(info,termcnt + " terms indexed in " + seconds + " seconds ...END at " + new Date());
		
		return info.toString();

	}

	@Override
	public String indexPublications() {
		long seconds = System.currentTimeMillis() / 1000;
		StringBuilder info = new StringBuilder();
		logAndCollect(info, "publications indexing...STARTING at " + new Date());

		String serverUrl = getServerUrl(PublicationIndex.NAME);
		logAndCollect(info,"Solr server: " + serverUrl); 		

		logAndCollect(info,"clearing publication index");
		SolrIndexer<Publication> indexer = new PublicationSolrIndexer(serverUrl, publicationService);
		indexer.clearDatabase("");

		logAndCollect(info,"getting publications");
        Set<Long> allpubids = globalPublicationService.findAllPublicationIds();

		logAndCollect(info,"start indexing of " + allpubids.size() + " publications");
		int pubcnt = 0;
		for (Long id : allpubids) {
			Publication currpub = publicationService.findPublicationById(id);
			if(currpub.getPublicationType().equals(PublicationType.ARTICLE)) {
			  indexer.convertAndAddDocsToSolr(currpub);
			  pubcnt++;
			  }
			if((pubcnt % 5000)==0)
				logAndCollect(info,pubcnt + "/" + allpubids.size() + " publications done");
		}
		indexer.flushRemainingDocsToSolr();
		
		logAndCollect(info,"committing");
		indexer.commit();
		seconds = (System.currentTimeMillis()/1000 - seconds);
		logAndCollect(info,pubcnt + " publications indexed in " + seconds + " seconds ...END at " + new Date());
		
		return info.toString();
	}

	private EntrySolrIndexer newSolrIndexer(String indexName, boolean isGold, StringBuilder info) {

        String serverUrl = getServerUrl(indexName);
        logAndCollect(info,"Solr server: " + serverUrl);

        return isGold ? EntrySolrIndexer.GoldOnly(serverUrl) : EntrySolrIndexer.SilverAndGold(serverUrl);
    }

	private String getServerUrl(String indexName) {
		String baseUrl = connFactory.getSolrBaseUrl();
		String indexUrl = configuration.getIndexByName(indexName).getUrl();
		return baseUrl + indexUrl;
	}

	private void logAndCollect(StringBuilder info,String message) {
		logger.info(message);
		info.append(message).append("\n");
	}
}
