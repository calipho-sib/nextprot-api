package org.nextprot.api.solr.indexation;

import org.apache.solr.common.SolrInputDocument;

/**
 * A solr document factory can calculate solr fields and boost information from any T-object
 * that will be indexed by solr
 */
public interface SolrDocumentFactory<T> {

    SolrInputDocument createSolrInputDocument(T object);
}
