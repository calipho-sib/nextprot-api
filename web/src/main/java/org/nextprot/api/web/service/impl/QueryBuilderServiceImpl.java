package org.nextprot.api.web.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.rdf.service.SparqlEndpoint;
import org.nextprot.api.rdf.service.SparqlService;
import org.nextprot.api.solr.Query;
import org.nextprot.api.solr.QueryRequest;
import org.nextprot.api.solr.SolrConfiguration;
import org.nextprot.api.solr.SolrIndex;
import org.nextprot.api.solr.SolrService;
import org.nextprot.api.user.domain.UserProteinList;
import org.nextprot.api.user.domain.UserQuery;
import org.nextprot.api.user.service.UserProteinListService;
import org.nextprot.api.user.service.UserQueryService;
import org.nextprot.api.user.utils.UserQueryUtils;
import org.nextprot.api.web.service.QueryBuilderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Joiner;

/**
 * 
 * Service that builds a nextprot query from a query request (http request)
 * 
 * @author dteixeira
 *
 */
@Service
public class QueryBuilderServiceImpl implements QueryBuilderService {

	@Autowired
	private SolrService queryService;
	@Autowired
	private SparqlService sparqlService;
	@Autowired
	private SparqlEndpoint sparqlEndpoint;

	@Autowired
	private UserQueryService userQueryService;
	@Autowired
	private UserProteinListService proteinListService;
	@Autowired
	private SolrConfiguration configuration;

	private static final Log Logger = LogFactory.getLog(QueryBuilderServiceImpl.class);

	@Override
	public Query buildQueryForSearch(QueryRequest queryRequest, String indexName) {

		if (queryRequest.hasAccs()) {
			Set<String> accessions = new HashSet<String>(queryRequest.getAccs());
			String queryString = "id:" + (accessions.size() > 1 ? "(" + Joiner.on(" ").join(accessions) + ")" : accessions.iterator().next());
			queryRequest.setQuery(queryString);

			return queryService.buildQueryForSearchIndexes(indexName, "pl_search", queryRequest);

		} else if (queryRequest.hasList()) {

			UserProteinList proteinList = this.proteinListService.getUserProteinListById(queryRequest.getListId());
			Set<String> accessions = proteinList.getAccessionNumbers();

			String queryString = "id:" + (accessions.size() > 1 ? "(" + Joiner.on(" ").join(accessions) + ")" : accessions.iterator().next());
			queryRequest.setQuery(queryString);

			return queryService.buildQueryForSearchIndexes(indexName, "pl_search", queryRequest);

		} else if (queryRequest.hasNextProtQuery()) {

			
			UserQuery uq = userQueryService.getUserQueryByPublicId(queryRequest.getQueryId());
			Set<String> accessions = new HashSet<String>(sparqlService.findEntries(uq.getSparql(), sparqlEndpoint.getUrl(), queryRequest.getSparqlTitle()));
			// In case there is no result
			if (accessions.isEmpty()) {
				// There is no entry with NULL value, so the result will be
				// empty, but the result structure will be maintaned (could be
				// replace by SearchResult factory where you create an emptry
				// result)
				accessions.add("NULL");
			}

			String queryString = "id:" + (accessions.size() > 1 ? "(" + Joiner.on(" ").join(accessions) + ")" : accessions.iterator().next());
			queryRequest.setQuery(queryString);

			return queryService.buildQueryForSearchIndexes(indexName, "pl_search", queryRequest);

		} else if (queryRequest.hasSparql()) {

			Set<String> accessions = new HashSet<String>(sparqlService.findEntries(queryRequest.getSparql(), sparqlEndpoint.getUrl(), queryRequest.getSparqlTitle()));

			// In case there is no result
			if (accessions.isEmpty()) {
				// There is no entry with NULL value, so the result will be
				// empty, but the result structure will be maintaned (could be
				// replace by SearchResult factory where you create an emptry
				// result)
				accessions.add("NULL");
			}

			String queryString = "id:" + (accessions.size() > 1 ? "(" + Joiner.on(" ").join(accessions) + ")" : accessions.iterator().next());
			queryRequest.setQuery(queryString);

			return queryService.buildQueryForSearchIndexes(indexName, "pl_search", queryRequest);

		} else {
			return queryService.buildQueryForSearchIndexes(indexName, "simple", queryRequest);
		}

	}

	@Override
	public Query buildQueryForProteinLists(String indexName, String queryString, String quality, String sort, String order, String start, String rows, String filter) {
		return queryService.buildQueryForProteinLists(indexName, queryString, quality, sort, order, start, rows, filter);
	}

	@Override
	public Query buildQueryForSearchIndexes(String indexName, String configurationName, QueryRequest request) {
		return queryService.buildQueryForSearchIndexes(indexName, configurationName, request);
	}

	@Override
	public Query buildQueryForAutocomplete(String indexName, String queryString, String quality, String sort, String order, String start, String rows, String filter) {
		return queryService.buildQueryForAutocomplete(indexName, queryString, quality, sort, order, start, rows, filter);
	}




}
