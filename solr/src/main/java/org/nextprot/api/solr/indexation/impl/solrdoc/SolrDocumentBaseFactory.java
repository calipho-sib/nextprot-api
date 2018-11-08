package org.nextprot.api.solr.indexation.impl.solrdoc;

import com.google.common.base.Preconditions;
import org.nextprot.api.solr.indexation.SolrDocumentFactory;

/**
 * A solr document factory can calculate solr fields and boost information from any T-object
 *
 * @param <T> object type to create SolrInputDocument from
 */
abstract class SolrDocumentBaseFactory<T> implements SolrDocumentFactory {

	final T solrizableObject;

	SolrDocumentBaseFactory(T solrizableObject) {

        Preconditions.checkNotNull(solrizableObject, "unable to solrize undefined object");

        this.solrizableObject = solrizableObject;
    }
}
