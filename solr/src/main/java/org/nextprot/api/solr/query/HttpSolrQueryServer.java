package org.nextprot.api.solr.query;

import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.SolrParams;

public class HttpSolrQueryServer implements SolrQueryServer {

    private final HttpSolrServer solrServer;

    public HttpSolrQueryServer(HttpSolrServer httpSolrServer) {

		solrServer = httpSolrServer;
	}

    @Override
    public QueryResponse query(SolrParams solrQuery, SolrRequest.METHOD method) throws SolrServerException {

        return solrServer.query(solrQuery, method);
    }
}
