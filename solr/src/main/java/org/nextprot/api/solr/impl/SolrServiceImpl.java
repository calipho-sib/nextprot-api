package org.nextprot.api.solr.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse.Collation;
import org.apache.solr.client.solrj.response.SpellCheckResponse.Suggestion;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.nextprot.api.commons.exception.SearchConnectionException;
import org.nextprot.api.commons.exception.SearchQueryException;
import org.nextprot.api.commons.utils.Pair;
import org.nextprot.api.solr.FieldConfigSet;
import org.nextprot.api.solr.IndexConfiguration;
import org.nextprot.api.solr.IndexField;
import org.nextprot.api.solr.IndexParameter;
import org.nextprot.api.solr.Query;
import org.nextprot.api.solr.QueryRequest;
import org.nextprot.api.solr.SearchResult;
import org.nextprot.api.solr.SearchResult.SearchResultFacet;
import org.nextprot.api.solr.SearchResult.SearchResultItem;
import org.nextprot.api.solr.SearchResult.SearchResultSpellcheck;
import org.nextprot.api.solr.SolrConfiguration;
import org.nextprot.api.solr.SolrConnectionFactory;
import org.nextprot.api.solr.SolrIndex;
import org.nextprot.api.solr.SolrService;
import org.nextprot.api.solr.SortConfig;
import org.nextprot.api.user.domain.UserList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.google.common.base.Joiner;

@Lazy
@Service
public class SolrServiceImpl implements SolrService {
	private static final Log Logger = LogFactory.getLog(SolrServiceImpl.class);

	@Autowired private SolrConnectionFactory connFactory;
	@Autowired private SolrConfiguration configuration;

	private final int DEFAULT_ROWS = 50;

	public SearchResult executeQuery(Query query) throws SearchQueryException {

		SolrIndex index = query.getIndex();
		SolrQuery solrQuery = solrQuerySetup(query);

		return executeSolrQuery(index, solrQuery);
	}

	public SearchResult executeCustomQuery(Query query, String[] fields) throws SearchQueryException {
		SolrIndex index = query.getIndex();
		SolrQuery solrQuery = solrQuerySetup(query);
		solrQuery.setFields(fields);

		return executeSolrQuery(index, solrQuery);
	}


	public SearchResult executeByIdQuery(Query query, String[] fields) {
		SolrIndex index = query.getIndex();

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(query.getQueryString());
		solrQuery.setFields(fields);
		solrQuery.setStart(query.getStart());
		solrQuery.setRows(query.getRows());

		return executeSolrQuery(index, solrQuery);
	}

	public SearchResult executeIdQuery(Query query) throws SearchQueryException {
		SolrIndex index = query.getIndex();

		if (index == null)
			index = this.configuration.getIndexByName(query.getIndexName());
		String configName = query.getConfigName();

		IndexConfiguration indexConfig = configName == null ? index
				.getDefaultConfig() : index.getConfig(query.getConfigName());

		SolrQuery solrQuery = buildSolrIdQuery(query, indexConfig);

		return executeSolrQuery(index, solrQuery);
	}

	public boolean checkAvailableIndex(String indexName) {
		return this.configuration.hasIndex(indexName);
	}


	public Query buildQuery(String indexName, String configuration, String queryString, String sort, String order,
			String start, String rows) {

		return buildQuery(indexName, configuration, queryString, null, sort, order, start, rows, null);
	}

	public Query buildQuery(String indexName, String configurationName, QueryRequest request) {

		return buildQuery(indexName, configurationName, request.getQuery(), request.getQuality(), request.getSort(),
				request.getOrder(), request.getStart(), request.getRows(), request.getFilter());
	}


	public Query buildQuery(String indexName, String configuration, String queryString, String quality, String sort, String order,
			String start, String rows, String filter) {

		String actualIndexName = indexName.equals("entry") && quality != null && quality.equals("gold") ? "gold-entry" : indexName;

		SolrIndex index = this.configuration.getIndexByName(actualIndexName);

		Query q = new Query(index).addQuery(queryString);
		q.setConfiguration(configuration);

		q.rows((rows != null) ? Integer.parseInt(rows) : DEFAULT_ROWS);
		q.start((start != null) ? Integer.parseInt(start) : 0);

		if(sort != null && sort.length() > 0)
			q.sort(sort);

		if(order != null && (order.equals(ORDER.asc.name()) || order.equals(ORDER.desc.name()))) {
			q.order(ORDER.valueOf(order));
		}

		q.setIndex(index);
		q.setIndexName(actualIndexName);

		if(filter != null && filter.length() > 0)
			q.addFilter(filter);

		return q;
	}

	private SolrQuery solrQuerySetup(Query query) throws SearchQueryException {
		SolrIndex index = query.getIndex();

		if (index == null)
			index = this.configuration.getIndexByName(query.getIndexName());
		String configName = query.getConfigName();

		IndexConfiguration indexConfig = configName == null ? index
				.getDefaultConfig() : index.getConfig(query.getConfigName());

		return buildSolrQuery(query, indexConfig);
	}

	private SolrQuery buildSolrIdQuery(Query query, IndexConfiguration indexConfig) throws SearchQueryException {
		SolrQuery solrQuery = new SolrQuery();

		String queryString = indexConfig.buildQuery(query);

		solrQuery.setQuery(queryString);
		solrQuery.setRows(0);
		solrQuery.set("facet", true);
		solrQuery.set("facet.field", "id");
		solrQuery.set("facet.query", queryString);
		solrQuery.set("facet.limit", 30000);

		return solrQuery;
	}



	/**
	 * Builds a SOLR Query according to the specified index configuration
	 *
	 * @param query
	 * @param indexConfig
	 * @return
	 */
	private SolrQuery buildSolrQuery(Query query, IndexConfiguration indexConfig)
			throws SearchQueryException {
		SolrQuery solrQuery = new SolrQuery();

		String queryString = indexConfig.buildQuery(query);

		String filter = query.getFilter();
		if (filter != null)
			queryString += " AND filters:" + filter;

		solrQuery.setQuery(queryString);
		solrQuery.setStart(query.getStart());
		solrQuery.setRows(query.getRows());
		solrQuery.setFields(indexConfig.getParameterQuery(IndexParameter.FL));
		solrQuery.set(IndexParameter.FL.name().toLowerCase(),
				indexConfig.getParameterQuery(IndexParameter.FL));
		solrQuery.set(IndexParameter.QF.name().toLowerCase(),
				indexConfig.getParameterQuery(IndexParameter.QF));
		solrQuery.set(IndexParameter.PF.name().toLowerCase(),
				indexConfig.getParameterQuery(IndexParameter.PF));
		solrQuery.set(IndexParameter.FN.name().toLowerCase(),
				indexConfig.getParameterQuery(IndexParameter.FN));
		solrQuery.set(IndexParameter.HI.name().toLowerCase(),
				indexConfig.getParameterQuery(IndexParameter.HI));

		Map<String, String> otherParameters = indexConfig.getOtherParameters();

		if (otherParameters != null)
			for (Entry<String, String> e : otherParameters.entrySet())
				solrQuery.set(e.getKey(), e.getValue());

		String sortName = query.getSort();
		SortConfig sortConfig = null;

		if (sortName != null) {
			sortConfig = indexConfig.getSortConfig(sortName);

			if (sortConfig == null)
				throw new SearchQueryException("sort " + sortName
						+ " does not exist");
		} else sortConfig = indexConfig.getDefaultSortConfiguration();

		if (query.getOrder() != null) {
			for (Pair<IndexField, ORDER> s : sortConfig.getSorting())
				solrQuery.addSort(s.getFirst().getName(), query.getOrder());

		} else {
			for (Pair<IndexField, ORDER> s : sortConfig.getSorting())
				solrQuery.addSort(s.getFirst().getName(), s.getSecond());
		}

		System.out.println("boost: "+sortConfig.getBoost());

//		function buildBoost(value) { return "sum(1.0,product(div(log(informational_score),6.0),div("+ value +",100.0)))"; }

		if(sortConfig.getBoost() != -1) {
			solrQuery.set("boost", "sum(1.0,product(div(log(informational_score),6.0),div("+ sortConfig.getBoost() +",100.0)))");
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
	private SearchResult executeSolrQuery(SolrIndex index, SolrQuery solrQuery) {
		SearchResult result = new SearchResult();
		SolrServer server = this.connFactory.getServer(index.getName());

		 Logger.info("server: "+index.getName()+" >> "+((HttpSolrServer)server).getBaseURL());

		 Logger.info("query: "+solrQuery.toString());

		try {
			QueryResponse response = server.query(solrQuery, METHOD.POST);
			result = buildSearchResult(solrQuery, index.getName(),
					index.getUrl(), response);
		} catch (SolrServerException e) {
			throw new SearchConnectionException("Could not connect to Solr server.");
		}
		return result;
	}

	private SearchResult buildSearchResult(SolrQuery query, String indexName,
			String url, QueryResponse response) {
		SearchResult results = new SearchResult(indexName, url);

		SolrDocumentList docs = response.getResults();
		List<SearchResultItem> res = new ArrayList<SearchResultItem>();

		SearchResultItem item = null;
		for (SolrDocument doc : docs) {

			item = new SearchResult.SearchResultItem();
			for (Entry<String, Object> e : doc.entrySet())
				item.addProperty(e.getKey(), e.getValue());

			res.add(item);
		}

		results.setResults(res);
		if(query.getStart() != null) results.setStart(query.getStart());

		results.setRows(query.getRows());
		results.setElapsedTime(response.getElapsedTime());
		results.setNumFound(docs.getNumFound());

		if (docs.getMaxScore() != null)
			results.setMaxScore(docs.getMaxScore());

		// Facets

		List<FacetField> facetFields = response.getFacetFields();

		if (facetFields != null) {
			SearchResultFacet facet = null;

			for (FacetField ff : facetFields) {
				facet = new SearchResultFacet(ff.getName());

				for (Count c : ff.getValues())
					facet.addFacetField(c.getName(), c.getCount());
				results.addSearchResultFacet(facet);
			}
		}

		// Spellcheck

		SpellCheckResponse spellcheckResponse = response
				.getSpellCheckResponse();

		if (spellcheckResponse != null) {
			SearchResultSpellcheck spellcheckResult = new SearchResultSpellcheck();

			List<Suggestion> suggestions = spellcheckResponse.getSuggestions();
			List<Collation> collations = spellcheckResponse
					.getCollatedResults();

			if (collations != null) {
				for (Collation c : collations)
					spellcheckResult.addCollation(c.getCollationQueryString(),
							c.getNumberOfHits());
			}

			if (suggestions != null)
				for (Suggestion s : suggestions)
					spellcheckResult.addSuggestions(s.getToken(),
							s.getAlternatives());

			results.setSpellCheck(spellcheckResult);
		}

		return results;
	}


@Override
public SearchResult getUserListSearchResult(UserList proteinList) throws SearchQueryException {

	Set<String> accessions = proteinList.getAccessions();

	String queryString = "id:" + (accessions.size() > 1 ? "(" + Joiner.on(" ").join(accessions) + ")" : accessions.iterator().next());

	SolrIndex index = this.configuration.getIndexByName("entry");
	IndexConfiguration indexConfig = index.getConfig("simple");

	FieldConfigSet fieldConfigSet = indexConfig.getConfigSet(IndexParameter.FL);
	Set<IndexField> fields = fieldConfigSet.getConfigs().keySet();
	getClass();

	String[] fieldNames = new String[fields.size()];

	Iterator<IndexField> it = fields.iterator();

	int counter = 0;
	while (it.hasNext()) {
		fieldNames[counter++] = it.next().getName();
	}

	Query query = new Query(index);
	query.addQuery(queryString);
	query.rows(50);
	// Query query = this.queryService.buildQuery(index, "simple", queryString, null, null, null, "0", "50", null, new String[0]);

	return this.executeByIdQuery(query, fieldNames);
}

}
