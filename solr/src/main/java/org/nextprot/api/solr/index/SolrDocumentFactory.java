package org.nextprot.api.solr.index;

import org.apache.solr.common.SolrInputDocument;

public interface SolrDocumentFactory {

    /**
     * @return instance of class representing solr fields and boost information
     */
    SolrInputDocument createSolrInputDocument();
}
