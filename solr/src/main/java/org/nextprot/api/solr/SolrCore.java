package org.nextprot.api.solr;


public interface SolrCore {

	String getName();
	String getUrl();
	IndexConfiguration getDefaultConfig();
	IndexConfiguration getConfig(String configName);
	Class<? extends SolrField> getFields();
	SolrField[] getFieldValues();
}
