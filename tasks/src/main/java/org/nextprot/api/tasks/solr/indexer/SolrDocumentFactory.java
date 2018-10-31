package org.nextprot.api.tasks.solr.indexer;

import com.google.common.base.Preconditions;
import org.apache.solr.common.SolrInputDocument;

/**
 * A solr document factory can calculate solr fields and boost information from any T-object
 *
 * @param <T> object type to create SolrInputDocument from
 */
abstract class SolrDocumentFactory<T> {

    protected final T solrizableObject;

    SolrDocumentFactory(T solrizableObject) {

        Preconditions.checkNotNull(solrizableObject);

        this.solrizableObject = solrizableObject;
    }

	/**
	 * @return instance of class representing solr fields and boost information
	 */
	public abstract SolrInputDocument calcSolrInputDocument();
}
