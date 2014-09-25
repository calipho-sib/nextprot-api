package org.nextprot.api.core.service;

import org.nextprot.api.commons.exception.SearchQueryException;
import org.nextprot.api.solr.Query;
import org.nextprot.api.solr.QueryRequest;
import org.nextprot.api.solr.SearchResult;


public interface SolrService {

	/**
	 * Execute a SOLR search query and return results
	 * @param query
	 */
	SearchResult executeQuery(Query query) throws SearchQueryException;

	/**
	 * Execute SOLR search query specifying the fields that should be returned
	 * @param query
	 * @param fields
	 * @return
	 * @throws SearchQueryException
	 */
	SearchResult executeCustomQuery(Query query, String[] fields) throws SearchQueryException;

	/**
	 * Returns only the IDs of the document which are the result of the query
	 * @param query
	 * @return
	 * @throws SearchQueryException
	 */
	SearchResult executeIdQuery(Query query) throws SearchQueryException;

	SearchResult executeByIdQuery(Query query, String[] fields);

	/**
	 * Verifies if the specified name matches a name of
	 * a registered index
	 * @param indexName
	 * @return
	 */
	boolean checkAvailableIndex(String indexName);

	Query buildQuery(String indexName, String configurationName, QueryRequest request);

	Query buildQuery(String indexName, String configuration,
			String queryString, String quality, String sort, String order,
			String start, String rows, String filter);

	SearchResult getUserListSearchResult(UserList proteinList) throws SearchQueryException;

//	Query buildQuery(SolrIndex index, String configuration,
//			String queryString, String quality, String sort, String order,
//			String start, String rows, String filter);

}
