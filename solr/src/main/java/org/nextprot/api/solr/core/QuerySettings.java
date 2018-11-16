package org.nextprot.api.solr.core;

public interface QuerySettings<F extends SolrField> {

	/** @return query config in given mode */
	QueryConfiguration<F> getConfig(SearchMode mode);

	/** @return default defined mode */
	SearchMode getDefaultMode();
}
