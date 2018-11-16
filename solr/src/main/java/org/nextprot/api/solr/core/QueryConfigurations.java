package org.nextprot.api.solr.core;

public interface QueryConfigurations<F extends SolrField> {

	QueryConfiguration<F> getConfig(SearchMode mode);
	SearchMode getDefaultMode();
}
