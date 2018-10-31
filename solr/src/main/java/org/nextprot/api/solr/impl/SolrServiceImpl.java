package org.nextprot.api.solr.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse.Collation;
import org.apache.solr.client.solrj.response.SpellCheckResponse.Suggestion;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.exception.SearchConnectionException;
import org.nextprot.api.commons.exception.SearchQueryException;
import org.nextprot.api.commons.utils.Pair;
import org.nextprot.api.solr.IndexConfiguration;
import org.nextprot.api.solr.IndexParameter;
import org.nextprot.api.solr.Query;
import org.nextprot.api.solr.QueryRequest;
import org.nextprot.api.solr.SearchResult;
import org.nextprot.api.solr.SearchResult.Facet;
import org.nextprot.api.solr.SearchResult.Spellcheck;
import org.nextprot.api.solr.SolrConfiguration;
import org.nextprot.api.solr.SolrConnectionFactory;
import org.nextprot.api.solr.SolrCore;
import org.nextprot.api.solr.SolrField;
import org.nextprot.api.solr.SolrService;
import org.nextprot.api.solr.SortConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

@Lazy
@Service
public class SolrServiceImpl implements SolrService {
	private static final Log Logger = LogFactory.getLog(SolrServiceImpl.class);
	private static final int DEFAULT_ROWS = 50;

	@Autowired
	private SolrConnectionFactory connFactory;
	@Autowired
	private SolrConfiguration configuration;

	private void logSolrQuery(String context, SolrQuery sq) {
		Set<String> params = new TreeSet<String>();
		for (String p : sq.getParameterNames()) params.add(p + " : " + sq.get(p));
		Logger.debug("SolrQuery ============================================================== in " + context);
		for (String p : params) {
			Logger.debug("SolrQuery " + p);
		}
	}

	public SearchResult executeQuery(Query query) throws SearchQueryException {
		SolrCore index = query.getIndex();
		SolrQuery solrQuery = solrQuerySetup(query);
		
		logSolrQuery("executeQuery",solrQuery);
		return executeSolrQuery(index, solrQuery);
	}

	public SearchResult executeCustomQuery(Query query, String[] fields) throws SearchQueryException {
		SolrCore index = query.getIndex();
		SolrQuery solrQuery = solrQuerySetup(query);
		solrQuery.setFields(fields);

		return executeSolrQuery(index, solrQuery);
	}

	
	public SearchResult executeIdQuery(Query query) throws SearchQueryException {
		SolrCore index = query.getIndex();

		if (index == null)
			index = this.configuration.getIndexByName(query.getIndexName());
		String configName = query.getConfigName();
		IndexConfiguration indexConfig = configName == null ? index.getDefaultConfig() : index.getConfig(query.getConfigName());
		Logger.debug("configName="+indexConfig.getName());

		SolrQuery solrQuery = buildSolrIdQuery(query, indexConfig);
		
		logSolrQuery("executeIdQuery", solrQuery);
		
		return executeSolrQuery(index, solrQuery);
	}

	public boolean checkAvailableIndex(String indexName) {
		return this.configuration.hasIndex(indexName);
	}

	private SolrQuery solrQuerySetup(Query query) throws SearchQueryException {
		SolrCore index = query.getIndex();

		if (index == null)
			index = this.configuration.getIndexByName(query.getIndexName());
		String configName = query.getConfigName();

		IndexConfiguration indexConfig = configName == null ? index.getDefaultConfig() : index.getConfig(query.getConfigName());

		return buildSolrQuery(query, indexConfig);
	}

	/*
	 * references: SearchController.searchIds() -> this.executeIdQuery() -> here
	 */
	@Override
	public SolrQuery buildSolrIdQuery(Query query, IndexConfiguration indexConfig) throws SearchQueryException {
		Logger.debug("Query index name:" + query.getIndexName());
		Logger.debug("Query config name: "+ query.getConfigName());
		String solrReadyQueryString = indexConfig.buildQuery(query);
		String filter = query.getFilter();
		if (filter != null)
			solrReadyQueryString += " AND filters:" + filter;

		Logger.debug("Solr-ready query       : " + solrReadyQueryString);
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
			for (Entry<String, String> e : otherParameters.entrySet())
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
			for (Pair<SolrField, ORDER> s : sortConfig.getSorting())
				solrQuery.addSort(s.getFirst().getName(), query.getOrder());

		} else {
			for (Pair<SolrField, ORDER> s : sortConfig.getSorting())
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
	 * @param index
	 * @param solrQuery
	 * @return
	 */
	private SearchResult executeSolrQuery(SolrCore index, SolrQuery solrQuery) {
		SearchResult result = new SearchResult();
		SolrServer server = this.connFactory.getServer(index.getName());

		// Logger.debug("server: " + index.getName() + " >> " +
		// ((HttpSolrServer) server).getBaseURL());
		// Logger.debug("query: " + solrQuery.toString());
		logSolrQuery("executeSolrQuery", solrQuery);

		try {
			QueryResponse response = server.query(solrQuery, METHOD.POST);
			result = buildSearchResult(solrQuery, index.getName(), index.getUrl(), response);
		} catch (SolrServerException e) {
			throw new SearchConnectionException("Could not connect to Solr server. Please contact support or try again later.");
		}
		return result;
	}

	private SearchResult buildSearchResult(SolrQuery query, String indexName, String url, QueryResponse response) {
		SearchResult results = new SearchResult(indexName, url);

		SolrDocumentList docs = response.getResults();
		Logger.debug("Response doc size:" + docs.size());
		List<Map<String, Object>> res = new ArrayList<>();

		Map<String, Object> item = null;
		for (SolrDocument doc : docs) {

			item = new HashMap<>();

			for (Entry<String, Object> e : doc.entrySet())
				item.put(e.getKey(), e.getValue());

			res.add(item);
		}

		results.addAllResults(res);
		if (query.getStart() != null)
			results.setStart(query.getStart());

		results.setRows(query.getRows());
		results.setElapsedTime(response.getElapsedTime());
		results.setFound(docs.getNumFound());

		if (docs.getMaxScore() != null)
			results.setScore(docs.getMaxScore());

		// Facets

		List<FacetField> facetFields = response.getFacetFields();
		Logger.debug("Response facet fields:" + facetFields.size());
		if (facetFields != null) {
			Facet facet = null;

			for (FacetField ff : facetFields) {
				facet = new Facet(ff.getName());
				Logger.debug("Response facet field:" + ff.getName() + " count:" + ff.getValueCount());

				for (Count c : ff.getValues())
					facet.addFacetField(c.getName(), c.getCount());
				results.addSearchResultFacet(facet);
			}
		}

		// Spellcheck

		SpellCheckResponse spellcheckResponse = response.getSpellCheckResponse();

		if (spellcheckResponse != null) {
			Spellcheck spellcheckResult = new Spellcheck();

			List<Suggestion> suggestions = spellcheckResponse.getSuggestions();
			List<Collation> collations = spellcheckResponse.getCollatedResults();

			if (collations != null) {
				for (Collation c : collations)
					spellcheckResult.addCollation(c.getCollationQueryString(), c.getNumberOfHits());
			}

			if (suggestions != null)
				for (Suggestion s : suggestions)
					spellcheckResult.addSuggestions(s.getToken(), s.getAlternatives());

			results.setSpellCheck(spellcheckResult);
		}

		return results;
	}

	/*
	 * @Override public SearchResult getUserListSearchResult(UserProteinList
	 * proteinList) throws SearchQueryException {
	 * 
	 * Set<String> accessions = proteinList.getAccessionNumbers();
	 * 
	 * String queryString = "id:" + (accessions.size() > 1 ? "(" +
	 * Joiner.on(" ").join(accessions) + ")" : accessions.iterator().next());
	 * 
	 * SolrIndex index = this.configuration.getIndexByName("entry");
	 * IndexConfiguration indexConfig = index.getConfig("simple");
	 * 
	 * FieldConfigSet fieldConfigSet =
	 * indexConfig.getConfigSet(IndexParameter.FL); Set<IndexField> fields =
	 * fieldConfigSet.getConfigs().keySet(); getClass();
	 * 
	 * String[] fieldNames = new String[fields.size()]; Iterator<IndexField> it
	 * = fields.iterator(); int counter = 0; while (it.hasNext()) {
	 * fieldNames[counter++] = it.next().getName(); }
	 * 
	 * Query query = new Query(index); query.addQuery(queryString);
	 * query.rows(50); // Query query = this.queryService.buildQuery(index,
	 * "simple", // queryString, null, null, null, "0", "50", null, new
	 * String[0]);
	 * 
	 * return this.executeByIdQuery(query, fieldNames); }
	 */

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

	private Query buildQuery(String indexName, String configurationName, QueryRequest request) {
		Logger.debug("calling buildQuery() with indexName=" + indexName + ", configName=" + configurationName) ;
		Logger.debug("\n--------------\nQueryRequest:\n--------------\n"+request.toPrettyString()+"\n--------------");
		Query q = buildQuery(indexName, configurationName, request.getQuery(), request.getQuality(), request.getSort(), request.getOrder(), request.getStart(), request.getRows(), request.getFilter());
		Logger.debug("\n--------------\nQuery:\n--------------\n" + q.toPrettyString() + "\n--------------");
		return q;
	}

	private Query buildQuery(String indexName, String configuration, String queryString, String quality, String sort, String order, String start, String rows, String filter) {

		String actualIndexName = indexName.equals("entry") && quality != null && quality.equalsIgnoreCase("gold") ? "gold-entry" : indexName;

		SolrCore index = this.configuration.getIndexByName(actualIndexName);

		Query q = new Query(index).addQuery(queryString);
		q.setConfiguration(configuration);

		q.rows((rows != null) ? Integer.parseInt(rows) : DEFAULT_ROWS);
		q.start((start != null) ? Integer.parseInt(start) : 0);

		if (sort != null && sort.length() > 0)
			q.sort(sort);

		if (order != null && (order.equals(ORDER.asc.name()) || order.equals(ORDER.desc.name()))) {
			q.order(ORDER.valueOf(order));
		}

		q.setIndex(index);
		q.setIndexName(actualIndexName);

		if (filter != null && filter.length() > 0)
			q.addFilter(filter);

		return q;
	}

	@Override
	public List<String> executeQueryAndGetAccessions(Query query) {

		List<String> accessions = new ArrayList<>();
		try {
			SearchResult result = executeQuery(query);
			for (Map<String, Object> item : result.getResults()) {
				accessions.add((String) item.get("id"));
			}
		} catch (SearchQueryException e) {
			e.printStackTrace();
			throw new NextProtException("An exception was thrown while searching");
		}
		return accessions;

	}

}
