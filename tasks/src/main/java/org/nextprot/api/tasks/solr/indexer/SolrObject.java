package org.nextprot.api.tasks.solr.indexer;

import com.google.common.base.Preconditions;
import org.apache.solr.common.SolrInputDocument;

/**
 * A solr object can compute its object state as solr fields and boost information
 *
 * @param <T> object type to index
 */
public abstract class SolrObject<T> {

    private final T object;

    SolrObject(T object) {

        Preconditions.checkNotNull(object);

        this.object = object;
    }

    final T getDocumentType() {
        return object;
    }

	/**
	 * @return object state as solr fields and boost information
	 */
	public abstract SolrInputDocument solrDocument();
}
