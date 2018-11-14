package org.nextprot.api.solr.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.nextprot.api.commons.exception.SearchConnectionException;
import org.nextprot.api.commons.exception.SearchQueryException;
import org.nextprot.api.commons.utils.Pair;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.publication.PublicationType;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.GlobalPublicationService;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.solr.core.SolrCore;
import org.nextprot.api.solr.core.SolrCoreRepository;
import org.nextprot.api.solr.core.SolrField;
import org.nextprot.api.solr.core.SolrHttpClient;
import org.nextprot.api.solr.indexation.BufferingSolrIndexer;
import org.nextprot.api.solr.indexation.impl.solrdoc.SolrCvTermDocumentFactory;
import org.nextprot.api.solr.indexation.impl.solrdoc.SolrEntryDocumentFactory;
import org.nextprot.api.solr.indexation.impl.solrdoc.SolrPublicationDocumentFactory;
import org.nextprot.api.solr.query.Query;
import org.nextprot.api.solr.query.SolrQueryClient;
import org.nextprot.api.solr.query.dto.QueryRequest;
import org.nextprot.api.solr.query.dto.SearchResult;
import org.nextprot.api.solr.query.impl.config.IndexConfiguration;
import org.nextprot.api.solr.query.impl.config.IndexParameter;
import org.nextprot.api.solr.query.impl.config.SortConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	public boolean checkSolrCore(String indexName, String quality) {

		return solrCoreRepository.hasSolrCore(getSolrCoreAliasName(indexName, quality));
	}

	@Override
	public Query buildQueryForAutocomplete(String indexName, String queryString, String quality, String sort, String order, String start, String rows, String filter) {
		return buildQuery(indexName, "autocomplete", queryString, quality, sort, order, start, rows, filter);
	}

	@Override
	public Query buildQueryForSearchIndexes(String indexName, String configurationName, QueryRequest request) {
		return this.buildQuery(indexName, configurationName, request);
	}

	@Override
	public Query buildQueryForProteinLists(String indexName, String queryString, String quality, String sort, String order, String start, String rows, String filter) {
		return buildQuery(indexName, "pl_search", queryString, quality, sort, order, start, rows, filter);
	}

    @Override
    public SearchResult executeQuery(Query query) throws SearchQueryException {
        SolrQuery solrQuery = solrQuerySetup(query);

        logSolrQuery("executeQuery",solrQuery);
        return executeSolrQuery(query.getSolrCore(), solrQuery);
    }

    @Override
    public SearchResult executeIdQuery(Query query) throws SearchQueryException {
        SolrCore solrCore = query.getSolrCore();

        if (solrCore == null) {
            solrCore = solrCoreRepository.getSolrCoreFromAlias(query.getIndexName());
        }
        String configName = query.getConfigName();
        IndexConfiguration indexConfig = (configName == null) ? solrCore.getDefaultConfig() : solrCore.getConfig(query.getConfigName());
        LOGGER.debug("configName="+indexConfig.getName());

        SolrQuery solrQuery = this.buildSolrQuery(query, indexConfig);

        logSolrQuery("executeIdQuery", solrQuery);

        return executeSolrQuery(solrCore, solrQuery);
    }

    /**
     * Builds a SOLR Query according to the specified index configuration
     *
     * @param query
     * @param indexConfig
     * @return
     */
    private SolrQuery buildSolrQuery(Query query, IndexConfiguration indexConfig) throws SearchQueryException {
        SolrQuery solrQuery = new SolrQuery();

        String queryString = indexConfig.buildQuery(query);

        String filter = query.getFilter();
        if (filter != null)
            queryString += " AND filters:" + filter;

        solrQuery.setQuery(queryString);
        solrQuery.setStart(query.getStart());
        solrQuery.setRows(query.getRows());
        solrQuery.setFields(indexConfig.getParameterQuery(IndexParameter.FL));
        solrQuery.set(IndexParameter.FL.name().toLowerCase(), indexConfig.getParameterQuery(IndexParameter.FL));
        solrQuery.set(IndexParameter.QF.name().toLowerCase(), indexConfig.getParameterQuery(IndexParameter.QF));
        solrQuery.set(IndexParameter.PF.name().toLowerCase(), indexConfig.getParameterQuery(IndexParameter.PF));
        solrQuery.set(IndexParameter.FN.name().toLowerCase(), indexConfig.getParameterQuery(IndexParameter.FN));
        solrQuery.set(IndexParameter.HI.name().toLowerCase(), indexConfig.getParameterQuery(IndexParameter.HI));

        Map<String, String> otherParameters = indexConfig.getOtherParameters();

        if (otherParameters != null)
            for (Map.Entry<String, String> e : otherParameters.entrySet())
                solrQuery.set(e.getKey(), e.getValue());

        String sortName = query.getSort();
        SortConfig sortConfig = null;

        if (sortName != null) {
            sortConfig = indexConfig.getSortConfig(sortName);

            if (sortConfig == null)
                throw new SearchQueryException("sort " + sortName + " does not exist");
        } else
            sortConfig = indexConfig.getDefaultSortConfiguration();

        if (query.getOrder() != null) {
            for (Pair<SolrField, SolrQuery.ORDER> s : sortConfig.getSorting())
                solrQuery.addSort(s.getFirst().getName(), query.getOrder());

        } else {
            for (Pair<SolrField, SolrQuery.ORDER> s : sortConfig.getSorting())
                solrQuery.addSort(s.getFirst().getName(), s.getSecond());
        }

        // function buildBoost(value) { return
        // "sum(1.0,product(div(log(informational_score),6.0),div("+ value
        // +",100.0)))"; }

        if (sortConfig.getBoost() != -1) {
            solrQuery.set("boost", "sum(1.0,product(div(log(informational_score),6.0),div(" + sortConfig.getBoost() + ",100.0)))");
        }

        return solrQuery;
    }

    /**
     * Perform the Solr query and return the results
     *
     * @param solrCore
     * @param solrQuery
     * @return
     */
    private SearchResult executeSolrQuery(SolrCore solrCore, SolrQuery solrQuery) {
        SolrQueryClient server = solrCore.newSolrClient();

        logSolrQuery("executeSolrQuery", solrQuery);

        try {
            QueryResponse response = server.query(solrQuery, SolrRequest.METHOD.POST);
            return buildSearchResult(solrQuery, solrCore, response);
        } catch (SolrServerException e) {
            throw new SearchConnectionException("Could not connect to Solr server. Please contact support or try again later.");
        }
    }

    private SearchResult buildSearchResult(SolrQuery solrQuery, SolrCore solrCore, QueryResponse response) {
        SearchResult results = new SearchResult(solrCore.getAlias().getName(), solrCore.getName());

        SolrDocumentList docs = response.getResults();
        LOGGER.debug("Response doc size:" + docs.size());
        List<Map<String, Object>> res = new ArrayList<>();

        Map<String, Object> item;
        for (SolrDocument doc : docs) {

            item = new HashMap<>();

            for (Map.Entry<String, Object> e : doc.entrySet())
                item.put(e.getKey(), e.getValue());

            res.add(item);
        }

        results.addAllResults(res);
        if (solrQuery.getStart() != null)
            results.setStart(solrQuery.getStart());

        results.setRows(solrQuery.getRows());
        results.setElapsedTime(response.getElapsedTime());
        results.setFound(docs.getNumFound());

        if (docs.getMaxScore() != null)
            results.setScore(docs.getMaxScore());

        // Facets

        List<FacetField> facetFields = response.getFacetFields();
        LOGGER.debug("Response facet fields:" + facetFields.size());
        if (facetFields != null) {
            SearchResult.Facet facet = null;

            for (FacetField ff : facetFields) {
                facet = new SearchResult.Facet(ff.getName());
                LOGGER.debug("Response facet field:" + ff.getName() + " count:" + ff.getValueCount());

                for (FacetField.Count c : ff.getValues())
                    facet.addFacetField(c.getName(), c.getCount());
                results.addSearchResultFacet(facet);
            }
        }

        // Spellcheck

        SpellCheckResponse spellcheckResponse = response.getSpellCheckResponse();

        if (spellcheckResponse != null) {
            SearchResult.Spellcheck spellcheckResult = new SearchResult.Spellcheck();

            List<SpellCheckResponse.Suggestion> suggestions = spellcheckResponse.getSuggestions();
            List<SpellCheckResponse.Collation> collations = spellcheckResponse.getCollatedResults();

            if (collations != null) {
                for (SpellCheckResponse.Collation c : collations)
                    spellcheckResult.addCollation(c.getCollationQueryString(), c.getNumberOfHits());
            }

            if (suggestions != null)
                for (SpellCheckResponse.Suggestion s : suggestions)
                    spellcheckResult.addSuggestions(s.getToken(), s.getAlternatives());

            results.setSpellCheck(spellcheckResult);
        }

        return results;
    }

    private Query buildQuery(String indexName, String configurationName, QueryRequest request) {
        LOGGER.debug("calling buildQuery() with indexName=" + indexName + ", configName=" + configurationName) ;
        LOGGER.debug("\n--------------\nQueryRequest:\n--------------\n"+request.toPrettyString()+"\n--------------");
        Query q = buildQuery(indexName, configurationName, request.getQuery(), request.getQuality(), request.getSort(), request.getOrder(), request.getStart(), request.getRows(), request.getFilter());
        LOGGER.debug("\n--------------\nQuery:\n--------------\n" + q.toPrettyString() + "\n--------------");
        return q;
    }

    private Query buildQuery(String indexName, String configuration, String queryString, String quality, String sort, String order, String start, String rows, String filter) {

        SolrCore solrCore = solrCoreRepository.getSolrCoreFromAlias(getSolrCoreAliasName(indexName, quality));

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

    private SolrQuery solrQuerySetup(Query query) throws SearchQueryException {
        SolrCore solrCore = query.getSolrCore();

        if (solrCore == null) {
            solrCore = solrCoreRepository.getSolrCoreFromAlias(query.getIndexName());
        }
        String configName = query.getConfigName();

        IndexConfiguration indexConfig = (configName == null) ? solrCore.getDefaultConfig() : solrCore.getConfig(query.getConfigName());

        return buildSolrQuery(query, indexConfig);
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

	private String getSolrCoreAliasName(String indexName, String quality) {

    	return (indexName.equals("entry") && (quality != null && quality.equalsIgnoreCase("gold"))) ? "gold-entry" : indexName;
	}
	/*
	 * references: SearchController.searchIds() -> this.executeIdQuery() -> here
	 */
    /*@Override
    public SolrQuery buildSolrIdQuery(Query query, IndexConfiguration indexConfig) throws SearchQueryException {
        LOGGER.debug("Query index name:" + query.getIndexName());
        LOGGER.debug("Query config name: "+ query.getConfigName());
        String solrReadyQueryString = indexConfig.buildQuery(query);
        String filter = query.getFilter();
        if (filter != null)
            solrReadyQueryString += " AND filters:" + filter;

        LOGGER.debug("Solr-ready query       : " + solrReadyQueryString);
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(solrReadyQueryString);
        solrQuery.setRows(0);
        solrQuery.set("facet", true);
        solrQuery.set("facet.field", "id");
        solrQuery.set("facet.method", "enum");
        solrQuery.set("facet.query", solrReadyQueryString);
        solrQuery.set("facet.limit", 30000);
        logSolrQuery("buildSolrIdQuery",solrQuery);
        return solrQuery;
    }*/
}
