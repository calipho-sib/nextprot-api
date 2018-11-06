package org.nextprot.api.solr.core;


import org.nextprot.api.solr.config.IndexConfiguration;

public interface SolrCore {

	String getName();
	String getUrl();
	IndexConfiguration getDefaultConfig();
	IndexConfiguration getConfig(String configName);
	SolrField[] getSchema();
}
