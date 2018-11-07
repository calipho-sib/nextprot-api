package org.nextprot.api.solr.indexation;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import java.util.Collection;

/**
 * A Solr server can create indexes from solr documents and clean indexes
 */
public interface SolrIndexationServer {

    UpdateResponse pushDocsForIndexation(Collection<SolrInputDocument> docs) throws SolrServerException, IOException;
    UpdateResponse deleteIndexes() throws SolrServerException, IOException;
    UpdateResponse commitIndexation() throws SolrServerException, IOException;
}
