package org.nextprot.api.solr.core.impl;

import com.google.common.base.Preconditions;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.nextprot.api.solr.core.SolrServer;
import org.nextprot.api.solr.indexation.SolrIndexationServer;
import org.nextprot.api.solr.indexation.impl.HttpSolrIndexationServer;
import org.nextprot.api.solr.query.SolrQueryServer;
import org.nextprot.api.solr.query.impl.HttpSolrQueryServer;

import java.io.IOException;
import java.util.Collection;

public class SolrCoreServer implements SolrServer {

    private final String baseUrl;
    private final SolrQueryServer solrQueryServer;
    private final SolrIndexationServer solrIndexationServer;

    public SolrCoreServer(HttpSolrServer httpSolrServer) {

        Preconditions.checkNotNull(httpSolrServer, "http solr server should be defined");

        baseUrl = httpSolrServer.getBaseURL();
        solrQueryServer = new HttpSolrQueryServer(httpSolrServer);
        solrIndexationServer = new HttpSolrIndexationServer(httpSolrServer);
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

    public String getBaseURL() {
        return baseUrl;
    }
}
