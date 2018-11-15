package org.nextprot.api.solr.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.nextprot.api.commons.exception.SearchConnectionException;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.publication.PublicationType;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.GlobalPublicationService;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.solr.core.Entity;
import org.nextprot.api.solr.core.SolrCore;
import org.nextprot.api.solr.core.SolrCoreRepository;
import org.nextprot.api.solr.core.SolrHttpClient;
import org.nextprot.api.solr.indexation.BufferingSolrIndexer;
import org.nextprot.api.solr.indexation.impl.solrdoc.SolrCvTermDocumentFactory;
import org.nextprot.api.solr.indexation.impl.solrdoc.SolrEntryDocumentFactory;
import org.nextprot.api.solr.indexation.impl.solrdoc.SolrPublicationDocumentFactory;
import org.nextprot.api.solr.query.Query;
import org.nextprot.api.solr.query.QueryConfiguration;
import org.nextprot.api.solr.query.QueryExecutor;
import org.nextprot.api.solr.query.dto.QueryRequest;
import org.nextprot.api.solr.query.dto.SearchResult;
import org.nextprot.api.solr.query.impl.config.IndexConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Service
public class SolrServiceImpl implements SolrService {

    private static final Log LOGGER = LogFactory.getLog(SolrServiceImpl.class);
    private static final int DEFAULT_ROWS = 50;

    @Autowired
    private SolrCoreRepository solrCoreRepository;
    @Autowired
    private TerminologyService terminologyService;
    @Autowired
    private PublicationService publicationService;
    @Autowired
    private GlobalPublicationService globalPublicationService;
    @Autowired
    private EntryBuilderService entryBuilderService;
    @Autowired
    private MasterIdentifierService masterIdentifierService;

	@Override
	public String initIndexEntries(boolean isGold) {

		long seconds = System.currentTimeMillis() / 1000;
		StringBuilder info = new StringBuilder();

		SolrCore.Alias alias = isGold ? SolrCore.Alias.GoldEntry : SolrCore.Alias.Entry;
		logAndCollect(info, "initializing index " + alias.getName() + "...STARTING at " + new Date());

		BufferingSolrIndexer solrIndexer = newBufferingSolrIndexer(alias, info);

		logAndCollect(info, "clearing index " + alias.getName());
		solrIndexer.clearIndexes();

		logAndCollect(info, "committing index " + alias.getName());
		solrIndexer.commitAndOptimize();

		seconds = (System.currentTimeMillis() / 1000 - seconds);
		logAndCollect(info, "index " + alias.getName() + " initialized in " + seconds + " seconds ...END at " + new Date());

		return info.toString();
	}

    @Override
    public String indexEntriesChromosome(boolean isGold, String chrName) {

        long seconds = System.currentTimeMillis() / 1000;
        StringBuilder info = new StringBuilder();

        SolrCore.Alias alias = isGold ? SolrCore.Alias.GoldEntry : SolrCore.Alias.Entry;
        logAndCollect(info, "adding entries to index " + alias.getName() + " from chromosome " + chrName + "...STARTING at " + new Date());

        BufferingSolrIndexer solrIndexer = newBufferingSolrIndexer(alias, info);

        logAndCollect(info, "getting entry list of chromosome " + chrName);
        List<String> allEntryAccessions = masterIdentifierService.findUniqueNamesOfChromosome(chrName);

        logAndCollect(info, "start indexing of " + allEntryAccessions.size() + " entries");
        int ecnt = 0;

	    SolrEntryDocumentFactory factory = new SolrEntryDocumentFactory(isGold);
        for (String entryAccession : allEntryAccessions) {
            ecnt++;

	        solrIndexer.indexDocument(factory.createSolrInputDocument(entryAccession));

            if ((ecnt % 300) == 0)
                logAndCollect(info, ecnt + "/" + allEntryAccessions.size() + " entries added to index " + alias.getName() + " for chromosome " + chrName);
        }

        logAndCollect(info, "committing index " + alias.getName());
	    solrIndexer.indexAndCommitLastDocuments();

        seconds = (System.currentTimeMillis() / 1000 - seconds);
        logAndCollect(info, "added entries to index " + alias.getName() + "from chromosome " + chrName + " in " + seconds + " seconds ...END at " + new Date());

        return info.toString();
    }

	@Override
	public String indexEntry(String entryAccession, boolean isGold) {

		long seconds = System.currentTimeMillis() / 1000;
		StringBuilder info = new StringBuilder();

		SolrCore.Alias alias = isGold ? SolrCore.Alias.GoldEntry : SolrCore.Alias.Entry;
		logAndCollect(info, "adding entry "+ entryAccession+" to index " + alias.getName() + "...STARTING at " + new Date());

		BufferingSolrIndexer solrIndexer = newBufferingSolrIndexer(alias, info);

		logAndCollect(info, "start indexing entry " + entryAccession);
		solrIndexer.indexDocument(new SolrEntryDocumentFactory(isGold).createSolrInputDocument(entryAccession));

		logAndCollect(info, "committing index " + alias.getName());
		solrIndexer.indexAndCommitLastDocuments();

		seconds = (System.currentTimeMillis() / 1000 - seconds);
		logAndCollect(info, "done in " + seconds + " seconds ...END at " + new Date());

		return info.toString();
	}

	@Override
    public String indexTerminologies() {

        long seconds = System.currentTimeMillis() / 1000;
        StringBuilder info = new StringBuilder();
        logAndCollect(info, "terms indexing...STARTING at " + new Date());

        BufferingSolrIndexer solrIndexer = newBufferingSolrIndexer(SolrCore.Alias.Term, info);

	    logAndCollect(info, "clearing term index");
	    solrIndexer.clearIndexes();

        logAndCollect(info, "getting terms for all terminologies");
	    List<CvTerm> allterms = terminologyService.findAllCVTerms();

        logAndCollect(info, "start indexing of " + allterms.size() + " terms");
        int termcnt = 0;

		SolrCvTermDocumentFactory factory = new SolrCvTermDocumentFactory();
        for (CvTerm term : allterms) {
	        solrIndexer.indexDocument(factory.createSolrInputDocument(term));
            termcnt++;
            if ((termcnt % 3000) == 0)
                logAndCollect(info, termcnt + "/" + allterms.size() + " cv terms done");
        }

        logAndCollect(info, "committing");
	    solrIndexer.indexAndCommitLastDocuments();
        seconds = (System.currentTimeMillis() / 1000 - seconds);
        logAndCollect(info, termcnt + " terms indexed in " + seconds + " seconds ...END at " + new Date());

        return info.toString();
    }

    @Override
    public String indexPublications() {

        long seconds = System.currentTimeMillis() / 1000;
        StringBuilder info = new StringBuilder();
        logAndCollect(info, "publications indexing...STARTING at " + new Date());

	    BufferingSolrIndexer solrIndexer = newBufferingSolrIndexer(SolrCore.Alias.Publication, info);

	    logAndCollect(info, "clearing publication index");
	    solrIndexer.clearIndexes();

        logAndCollect(info, "getting publications");
        Set<Long> allpubids = globalPublicationService.findAllPublicationIds();

        logAndCollect(info, "start indexing of " + allpubids.size() + " publications");
        int pubcnt = 0;
	    SolrPublicationDocumentFactory factory = new SolrPublicationDocumentFactory();
	    for (Long id : allpubids) {
            Publication currpub = publicationService.findPublicationById(id);
            if (currpub.getPublicationType().equals(PublicationType.ARTICLE)) {

	            solrIndexer.indexDocument(factory.createSolrInputDocument(currpub));
                pubcnt++;
            }
            if ((pubcnt % 5000) == 0)
                logAndCollect(info, pubcnt + "/" + allpubids.size() + " publications done");
        }

        logAndCollect(info, "committing");
	    solrIndexer.indexAndCommitLastDocuments();
        seconds = (System.currentTimeMillis() / 1000 - seconds);
        logAndCollect(info, pubcnt + " publications indexed in " + seconds + " seconds ...END at " + new Date());

        return info.toString();
    }

	@Override
	public boolean checkSolrCore(Entity entity, String quality) {

		return solrCoreRepository.hasSolrCore(SolrCore.Alias.fromEntityAndQuality(entity, quality));
	}

	@Override
	public Query buildQueryForAutocomplete(Entity entity, String queryString, String quality, String sort, String order, String start, String rows, String filter) {
		return buildQuery(entity, "autocomplete", queryString, quality, sort, order, start, rows, filter);
	}

	@Override
	public Query buildQueryForSearchIndexes(Entity entity, String configurationName, QueryRequest request) {
		return this.buildQuery(entity, configurationName, request);
	}

	@Override
	public Query buildQueryForProteinLists(Entity entity, String queryString, String quality, String sort, String order, String start, String rows, String filter) {
		return buildQuery(entity, "pl_search", queryString, quality, sort, order, start, rows, filter);
	}

	private Query buildQuery(Entity entity, String configurationName, QueryRequest request) {
		LOGGER.debug("calling buildQuery() with entityName=" + entity.getName() + ", configName=" + configurationName) ;
		LOGGER.debug("\n--------------\nQueryRequest:\n--------------\n"+request.toPrettyString()+"\n--------------");
		Query q = buildQuery(entity, configurationName, request.getQuery(), request.getQuality(), request.getSort(), request.getOrder(), request.getStart(), request.getRows(), request.getFilter());
		LOGGER.debug("\n--------------\nQuery:\n--------------\n" + q.toPrettyString() + "\n--------------");
		return q;
	}

	private Query buildQuery(Entity entity, String configuration, String queryString, String quality, String sort, String order, String start, String rows, String filter) {

		SolrCore solrCore = solrCoreRepository.getSolrCore(SolrCore.Alias.fromEntityAndQuality(entity, quality));

		Query q = new Query(solrCore).addQuery(queryString);
		q.setConfiguration(configuration);

		q.rows((rows != null) ? Integer.parseInt(rows) : DEFAULT_ROWS);
		q.start((start != null) ? Integer.parseInt(start) : 0);

		if (sort != null && sort.length() > 0)
			q.sort(sort);

		if (order != null && (order.equals(SolrQuery.ORDER.asc.name()) || order.equals(SolrQuery.ORDER.desc.name()))) {
			q.order(SolrQuery.ORDER.valueOf(order));
		}

		q.setIndexName(solrCore.getAlias().getName());

		if (filter != null && filter.length() > 0)
			q.addFilter(filter);

		return q;
	}

	@Override
    public SearchResult executeIdQuery(Query query) {

		SolrCore core = getSolrCore(query);

		QueryExecutor executor = new QueryExecutor(core);

		SolrQuery solrQuery = getConfig(core, query.getConfigName()).convertIdQuery(query);
		logSolrQuery("executeIdQuery", solrQuery);

		try {
			return executor.execute(solrQuery);
		} catch (SolrServerException e) {
			throw new SearchConnectionException("Could not connect to Solr server. Please contact support or try again later.");
		}
	}

    /**
     * Perform the Solr query and return the results
     */
    @Override
    public SearchResult executeQuery(Query query) throws QueryConfiguration.MissingSortConfigException {

	    SolrCore core = getSolrCore(query);

    	QueryExecutor executor = new QueryExecutor(core);

    	try {
		    SolrQuery solrQuery = getConfig(core, query.getConfigName()).convertQuery(query);
		    return executor.execute(solrQuery);
	    } catch (SolrServerException e) {
            throw new SearchConnectionException("Could not connect to Solr server. Please contact support or try again later.");
        }
    }

    private IndexConfiguration getConfig(SolrCore solrCore, String configName) {

	    return (configName == null) ? solrCore.getDefaultConfig() : solrCore.getConfig(configName);
    }

    private SolrCore getSolrCore(Query query) {

	    SolrCore core = query.getSolrCore();

	    if (core == null) {
		    if (query.getIndexName() != null) {
			    return solrCoreRepository.getSolrCoreFromAlias(query.getIndexName());
		    }
		    else {
			    throw new SearchConnectionException("Could not connect to unknown Solr server.");
		    }
	    }
	    return core;
    }

    private void logSolrQuery(String context, SolrQuery sq) {
        Set<String> params = new TreeSet<>();
        for (String p : sq.getParameterNames()) params.add(p + " : " + sq.get(p));
        LOGGER.debug("SolrQuery ============================================================== in " + context);
        for (String p : params) {
            LOGGER.debug("SolrQuery " + p);
        }
    }

    // TODO: should will defined different buffer size depending on the entity to index
    private BufferingSolrIndexer newBufferingSolrIndexer(SolrCore.Alias alias, StringBuilder info) {

        SolrHttpClient solrClient = solrCoreRepository.getSolrCore(alias).newSolrClient();
	    logAndCollect(info, "Solr server: " + solrClient.getBaseURL());

	    return new BufferingSolrIndexer(solrClient);
    }

	private void logAndCollect(StringBuilder info, String message) {
        LOGGER.info(message);
		info.append(message).append("\n");
	}
}
