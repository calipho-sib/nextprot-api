package org.nextprot.api.web.service;

import org.nextprot.api.solr.Query;
import org.nextprot.api.solr.dto.QueryRequest;

public interface QueryBuilderService {

	public Query buildQueryForSearch(QueryRequest queryRequest, String indexName);

	
	public Query buildQueryForProteinLists(String indexName, String queryString, String quality, String sort, String order, String start, String rows, String filter);
	public Query buildQueryForSearchIndexes(String indexName, String configurationName, QueryRequest request);
	public Query buildQueryForAutocomplete(String indexName, String queryString, String quality, String sort, String order, String start, String rows, String filter);

}
