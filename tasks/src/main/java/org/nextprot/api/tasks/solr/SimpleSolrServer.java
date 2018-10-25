package org.nextprot.api.tasks.solr;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import java.util.Collection;

/**
 * A simplified Solr Server
 */
public interface SimpleSolrServer {

    UpdateResponse add(Collection<SolrInputDocument> docs) throws SolrServerException, IOException;
    UpdateResponse deleteByQuery(String query) throws SolrServerException, IOException;
    UpdateResponse commit() throws SolrServerException, IOException;
    UpdateResponse optimize() throws SolrServerException, IOException;
}
