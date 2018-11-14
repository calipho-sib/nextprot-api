package org.nextprot.api.solr.core.impl.cores;

import com.google.common.base.Preconditions;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.nextprot.api.solr.indexation.SolrIndexationServer;
import org.nextprot.api.solr.indexation.impl.HttpSolrIndexationServer;
import org.nextprot.api.solr.query.SolrQueryServer;
import org.nextprot.api.solr.query.impl.HttpSolrQueryServer;

import java.io.IOException;
import java.util.Collection;

public class SolrCoreServerNew implements SolrServerNew {

	private final String coreName;
    private final String baseUrl;
    private final SolrQueryServer solrQueryServer;
    private final SolrIndexationServer solrIndexationServer;

    public SolrCoreServerNew(String coreName, HttpSolrServer httpSolrServer) {

        Preconditions.checkNotNull(httpSolrServer, "http solr server should be defined");

        this.coreName = coreName;
        baseUrl = formatBaseUrl(httpSolrServer.getBaseURL());
        solrQueryServer = new HttpSolrQueryServer(httpSolrServer);
        solrIndexationServer = new HttpSolrIndexationServer(httpSolrServer);
    }

    private String formatBaseUrl(String baseUrl) {

	    return (baseUrl.charAt(baseUrl.length()-1) != '/') ? baseUrl + '/' : baseUrl;
    }

    @Override
    public UpdateResponse indexDocuments(Collection<SolrInputDocument> docs) throws SolrServerException, IOException {
        return solrIndexationServer.indexDocuments(docs);
    }

    @Override
    public UpdateResponse deleteIndexes() throws SolrServerException, IOException {
        return solrIndexationServer.deleteIndexes();
    }

    @Override
    public UpdateResponse commitAndOptimize() throws SolrServerException, IOException {
        return solrIndexationServer.commitAndOptimize();
    }

    @Override
    public QueryResponse query(SolrParams params, SolrRequest.METHOD method) throws SolrServerException {
        return solrQueryServer.query(params, method);
    }

	@Override
	public String getBaseURL() {
		return baseUrl;
	}

	@Override
	public String getURL() {
		return baseUrl + coreName;
	}
}
