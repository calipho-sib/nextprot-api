package org.nextprot.api.solr.core.impl;

import com.google.common.base.Preconditions;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.nextprot.api.solr.core.SolrHttpClient;
import org.nextprot.api.solr.indexation.SolrIndexationClient;
import org.nextprot.api.solr.indexation.impl.HttpSolrIndexationClient;
import org.nextprot.api.solr.query.SolrQueryClient;
import org.nextprot.api.solr.query.impl.HttpSolrQueryClient;

import java.io.IOException;
import java.util.Collection;

public class SolrCoreHttpClient implements SolrHttpClient {

	private final String coreName;
	private final String baseUrl;
    private final SolrQueryClient solrQueryClient;
    private final SolrIndexationClient solrIndexationClient;

    public SolrCoreHttpClient(String coreName, HttpSolrServer httpSolrServer) {

        Preconditions.checkNotNull(coreName, "solr core name should be defined");
        Preconditions.checkNotNull(httpSolrServer, "http solr server should be defined");

	    this.coreName = coreName;
	    baseUrl = removeLastPotentialSlash(httpSolrServer.getBaseURL());
        solrQueryClient = new HttpSolrQueryClient(httpSolrServer);
        solrIndexationClient = new HttpSolrIndexationClient(httpSolrServer);
    }

	private String removeLastPotentialSlash(String baseUrl) {

		return (baseUrl.charAt(baseUrl.length()-1) == '/') ? baseUrl.substring(0, baseUrl.length()-1) : baseUrl;
	}

    @Override
    public UpdateResponse indexDocuments(Collection<SolrInputDocument> docs) throws SolrServerException, IOException {
        return solrIndexationClient.indexDocuments(docs);
    }

    @Override
    public UpdateResponse deleteIndexes() throws SolrServerException, IOException {
        return solrIndexationClient.deleteIndexes();
    }

    @Override
    public UpdateResponse commitAndOptimize() throws SolrServerException, IOException {
        return solrIndexationClient.commitAndOptimize();
    }

    @Override
    public QueryResponse query(SolrParams params, SolrRequest.METHOD method) throws SolrServerException {
        return solrQueryClient.query(params, method);
    }

	@Override
	public String getBaseURL() {
        return baseUrl;
    }

	@Override
	public String getURL() {
		return baseUrl + '/' + coreName;
	}
}
