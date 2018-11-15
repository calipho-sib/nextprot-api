package org.nextprot.api.solr.core;

public interface QueryConfigurations {

	QueryConfiguration getConfig(SearchMode mode);
	QueryConfiguration getDefaultConfig();
}
