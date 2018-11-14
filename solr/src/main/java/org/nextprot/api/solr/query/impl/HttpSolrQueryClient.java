package org.nextprot.api.solr.query.impl;

import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.SolrParams;
import org.nextprot.api.solr.query.SolrQueryClient;

public class HttpSolrQueryClient implements SolrQueryClient {

    private final HttpSolrServer solrServer;

    public HttpSolrQueryClient(HttpSolrServer httpSolrServer) {

		solrServer = httpSolrServer;
	}

    @Override
    public QueryResponse query(SolrParams solrQuery, SolrRequest.METHOD method) throws SolrServerException {

        return solrServer.query(solrQuery, method);
    }
}
