package org.nextprot.api.solr.core;


import org.nextprot.api.commons.exception.SearchConfigException;
import org.nextprot.api.solr.AutocompleteConfiguration;
import org.nextprot.api.solr.IndexConfiguration;
import org.nextprot.api.solr.SolrCore;
import org.nextprot.api.solr.SolrField;
import org.nextprot.api.solr.SortConfig;

import java.util.HashMap;
import java.util.Map;

public abstract class CoreTemplate implements SolrCore {

	protected String name;
	protected String url;
	protected IndexConfiguration defaultConfiguration;
	protected SortConfig[] sortConfigurations;
	protected AutocompleteConfiguration autocompleteConfiguration;
	protected String defaultConfigName;
	protected Map<String, IndexConfiguration> configurations = new HashMap<>();

	protected CoreTemplate(String name, String url) {
		this.name = name;
		this.url = url;
		sortConfigurations = newSortConfigurations();
		defaultConfiguration = newDefaultConfiguration();
		autocompleteConfiguration = newAutoCompleteConfiguration(defaultConfiguration);
		setupConfigurations();
	}

	protected abstract IndexConfiguration newDefaultConfiguration();
	protected abstract AutocompleteConfiguration newAutoCompleteConfiguration(IndexConfiguration defaultConfiguration);
	protected abstract SortConfig[] newSortConfigurations();

	protected abstract void setupConfigurations();

	public abstract SolrField[] getSchema();

	protected void addConfiguration(IndexConfiguration config) {
		this.configurations.put(config.getName(), config);
	}

	protected void setConfigAsDefault(String configName) {
		if(this.configurations.containsKey(configName))
			this.defaultConfigName = configName;
		else throw new SearchConfigException("Cannot set configuration "+configName+" since it does not exist");
	}

	public IndexConfiguration getDefaultConfig() {
		if(this.defaultConfigName != null)
			return this.configurations.get(defaultConfigName);
		else if(this.configurations.size() == 1)
			return this.configurations.entrySet().iterator().next().getValue();		// retrieve only element
		else throw new SearchConfigException("Default configuration has not been properly set for index "+name);
	}

	public IndexConfiguration getConfig(String configName) {
		if (this.configurations.containsKey(configName))
			return this.configurations.get(configName);
		else
			throw new SearchConfigException("Configuration "+configName+" does not exist");
	}

	public String getName() {
		return this.name;
	}

	public String getUrl() {
		return this.url;
	}
}
