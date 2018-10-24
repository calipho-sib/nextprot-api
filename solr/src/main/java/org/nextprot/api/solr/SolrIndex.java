package org.nextprot.api.solr;


public interface SolrIndex {

	String getName();
	String getUrl();
	IndexConfiguration getDefaultConfig();
	IndexConfiguration getConfig(String configName);
	Class<? extends IndexField> getFields();
	IndexField[] getFieldValues();
}
