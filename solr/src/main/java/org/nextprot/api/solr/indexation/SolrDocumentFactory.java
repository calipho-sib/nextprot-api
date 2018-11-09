package org.nextprot.api.solr.indexation;

import org.apache.solr.common.SolrInputDocument;

/**
 * A solr document factory can calculate solr fields and boost information from any object to be indexed by solr
 */
public interface SolrDocumentFactory {

    /**
     * @return instance of class representing solr fields and boost information
     */
    SolrInputDocument createSolrInputDocument();
}
