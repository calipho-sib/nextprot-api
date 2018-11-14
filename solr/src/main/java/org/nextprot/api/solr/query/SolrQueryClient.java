package org.nextprot.api.solr.query;

import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.SolrParams;

/**
 * A Solr client can make queries on indexed solr documents
 */
public interface SolrQueryClient {

    QueryResponse query(SolrParams params, SolrRequest.METHOD method) throws SolrServerException;
}
