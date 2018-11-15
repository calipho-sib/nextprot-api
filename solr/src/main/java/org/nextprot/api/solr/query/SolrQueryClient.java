package org.nextprot.api.solr.query;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;

/**
 * A Solr client can make queries on indexed solr documents
 */
public interface SolrQueryClient {

    QueryResponse query(SolrQuery query, SolrRequest.METHOD method) throws SolrServerException;
}
