package org.nextprot.api.web.service;

import org.nextprot.api.solr.query.Query;
import org.nextprot.api.solr.query.dto.QueryRequest;

public interface QueryBuilderService {

	Query buildQueryForSearch(QueryRequest queryRequest, String indexName);
	Query buildQueryForProteinLists(String indexName, String queryString, String quality, String sort, String order, String start, String rows, String filter);
	Query buildQueryForSearchIndexes(String indexName, String configurationName, QueryRequest request);
	Query buildQueryForAutocomplete(String indexName, String queryString, String quality, String sort, String order, String start, String rows, String filter);
}
