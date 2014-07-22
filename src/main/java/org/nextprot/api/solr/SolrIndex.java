package org.nextprot.api.solr;

import org.nextprot.api.solr.IndexTemplate.ConfigurationName;
import org.nextprot.api.solr.config.IndexConfiguration;




public interface SolrIndex {

	String getName();
	String getUrl();
	IndexConfiguration getDefaultConfig();
	IndexConfiguration getConfig(String configName);
	
	Class<? extends ConfigurationName> getConfigNames();
	Class<? extends IndexField> getFields();
}
