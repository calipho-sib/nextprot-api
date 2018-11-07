package org.nextprot.api.solr.indexation;

import org.apache.solr.common.SolrInputDocument;

public interface SolrDocumentFactory {

    /**
     * @return instance of class representing solr fields and boost information
     */
    SolrInputDocument createSolrInputDocument();
}
