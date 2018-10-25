package org.nextprot.api.tasks.solr;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import java.util.Collection;

public class SimpleHttpSolrServer implements SimpleSolrServer {

    private final HttpSolrServer solrServer;

    public SimpleHttpSolrServer(String url) {

        solrServer = new HttpSolrServer(url);
    }

    @Override
    public final UpdateResponse add(Collection<SolrInputDocument> docs) throws SolrServerException, IOException {
        return solrServer.add(docs);
    }

    @Override
    public final UpdateResponse deleteByQuery(String query) throws SolrServerException, IOException {
        return solrServer.deleteByQuery(query);
    }

    @Override
    public final UpdateResponse commit() throws SolrServerException, IOException {
        return solrServer.commit();
    }

    @Override
    public final UpdateResponse optimize() throws SolrServerException, IOException {
        return solrServer.optimize();
    }
}
