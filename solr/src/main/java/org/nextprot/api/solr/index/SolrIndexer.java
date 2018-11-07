package org.nextprot.api.solr.index;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import java.util.Collection;

/**
 * A Solr indexer can create indexes from solr documents and clean indexes
 */
public interface SolrIndexer {

    UpdateResponse performIndexation(Collection<SolrInputDocument> docs) throws SolrServerException, IOException;
    UpdateResponse deleteIndexes() throws SolrServerException, IOException;
    UpdateResponse execute() throws SolrServerException, IOException;
}
