package org.nextprot.api.tasks.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.service.DbXrefService;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.solr.SolrConfiguration;
import org.nextprot.api.solr.SolrConnectionFactory;
import org.nextprot.api.solr.index.CvIndex;
import org.nextprot.api.solr.index.EntryIndex;
import org.nextprot.api.solr.index.GoldEntryIndex;
import org.nextprot.api.solr.index.PublicationIndex;
import org.nextprot.api.tasks.service.SolrIndexingService;
import org.nextprot.api.tasks.solr.indexer.CvTermSolrIndexer;
import org.nextprot.api.tasks.solr.indexer.EntryBaseSolrIndexer;
import org.nextprot.api.tasks.solr.indexer.EntryGoldSolrIndexer;
import org.nextprot.api.tasks.solr.indexer.EntrySolrIndexer;
import org.nextprot.api.tasks.solr.indexer.PublicationSolrindexer;
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
	@Autowired private PublicationService publicationService;
	@Autowired private MasterIdentifierService MasterEntryService ;
	@Autowired private EntryBuilderService entryBuilderService ;
	@Autowired private MasterIdentifierService masterIdentifierService;
	@Autowired private DbXrefService dbxrefService;
	
	@Override
	public String IndexEntriesChromosome(boolean isGold, String chrName) {

		long seconds = System.currentTimeMillis() / 1000;
		StringBuilder info = new StringBuilder();
		
		String indexName = isGold ?  GoldEntryIndex.NAME : EntryIndex.NAME;
		logAndCollect(info, "adding entries to index " + indexName + "from chromosome " + chrName + "...STARTING at " + new Date());

		String serverUrl = getServerUrl(indexName);
		logAndCollect(info,"Solr server: " + serverUrl); 

		EntryBaseSolrIndexer indexer = isGold ? new EntryGoldSolrIndexer(serverUrl) : new EntrySolrIndexer(serverUrl);
		indexer.setTerminologyservice(terminologyService);
		indexer.setEntryBuilderService(entryBuilderService);
		indexer.setDbxrefservice(dbxrefService);

		logAndCollect(info,"getting entry list of chromosome " + chrName);
		List<String> allentryids = masterIdentifierService.findUniqueNamesOfChromosome(chrName);

		logAndCollect(info,"start indexing of " + allentryids.size() + " entries");
		int ecnt=0;
		for (String id : allentryids) {
			ecnt++;
			Entry currentry = entryBuilderService.buildWithEverything(id);
			indexer.add(currentry);
			if((ecnt % 1000) == 0)
				logAndCollect(info,ecnt + "/" + allentryids.size() + " entries done");
		}
		indexer.addRemaing();
		
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
		
		String indexName = isGold ?  GoldEntryIndex.NAME : EntryIndex.NAME;
		logAndCollect(info, "initializing index " + indexName + "...STARTING at " + new Date());

		String serverUrl = getServerUrl(indexName);
		logAndCollect(info,"Solr server: " + serverUrl); 		

		logAndCollect(info,"clearing index " + indexName);
		SolrIndexer indexer = isGold ? new EntryGoldSolrIndexer(serverUrl) : new EntrySolrIndexer(serverUrl);
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
		long seconds = System.currentTimeMillis() / 1000;
		StringBuilder info = new StringBuilder();
		logAndCollect(info, "publications indexing...STARTING at " + new Date());

		String serverUrl = getServerUrl(PublicationIndex.NAME);
		logAndCollect(info,"Solr server: " + serverUrl); 		

		logAndCollect(info,"clearing publication index");
		SolrIndexer<Publication> indexer = new PublicationSolrindexer(serverUrl);
		List<Long> allpubids;
		indexer.clearDatabase("");

		logAndCollect(info,"getting publications");
		allpubids = publicationService.findAllPublicationIds();

		logAndCollect(info,"start indexing of " + allpubids.size() + " publications");
		int pubcnt = 0;
		for (Long id : allpubids) {
			Publication currpub = publicationService.findPublicationById(id);
			if(currpub.getPublicationType().equals("ARTICLE")) {
			  indexer.add(currpub);
			  pubcnt++;
			  }
			if((pubcnt % 5000)==0)
				logAndCollect(info,pubcnt + "/" + allpubids.size() + " publications done");
		}
		indexer.addRemaing();
		
		logAndCollect(info,"comitting");
		indexer.commit();
		seconds = (System.currentTimeMillis()/1000 - seconds);
		logAndCollect(info,pubcnt + " publications indexed in " + seconds + " seconds ...END at " + new Date());
		
		return info.toString();

	}
	
	private String getServerUrl(String indexName) {
		String baseUrl = connFactory.getSolrBaseUrl();
		String indexUrl = configuration.getIndexByName(indexName).getUrl();
		String serverUrl = baseUrl + indexUrl;
		return serverUrl;	
	}

	private void logAndCollect(StringBuilder info,String message) {
		logger.info(message);
		info.append(message).append("\n");
	}


	
}
