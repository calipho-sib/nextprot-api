package org.nextprot.api.solr.indexation;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import java.util.Collection;

public class HttpSolrIndexationServer implements SolrIndexationServer {

    private final HttpSolrServer solrServer;

    public HttpSolrIndexationServer(HttpSolrServer httpSolrServer) {

		solrServer = httpSolrServer;
	}

    @Override
    public final UpdateResponse pushDocsForIndexation(Collection<SolrInputDocument> docs) throws SolrServerException, IOException {
        return solrServer.add(docs);
    }

    @Override
    public final UpdateResponse deleteIndexes() throws SolrServerException, IOException {
        return solrServer.deleteByQuery("*:*");
    }

    @Override
    public final UpdateResponse commitIndexation() throws SolrServerException, IOException {
    	solrServer.commit();
        return solrServer.optimize();
    }
}
