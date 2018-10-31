package org.nextprot.api.tasks.solr;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import java.util.Collection;

public class HttpSolrIndexer implements SolrIndexer {

    private final HttpSolrServer solrServer;

    public HttpSolrIndexer(String solrServerUrl) {

        solrServer = new HttpSolrServer(solrServerUrl);
    }

    @Override
    public final UpdateResponse performIndexation(Collection<SolrInputDocument> docs) throws SolrServerException, IOException {
        return solrServer.add(docs);
    }

    @Override
    public final UpdateResponse deleteIndexes() throws SolrServerException, IOException {
        return solrServer.deleteByQuery("*:*");
    }

    @Override
    public final UpdateResponse execute() throws SolrServerException, IOException {
    	solrServer.commit();
        return solrServer.optimize();
    }
}
