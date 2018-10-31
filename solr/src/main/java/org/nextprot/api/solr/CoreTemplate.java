package org.nextprot.api.solr;


import org.nextprot.api.commons.exception.SearchConfigException;

import java.util.HashMap;
import java.util.Map;

public abstract class CoreTemplate implements SolrCore {

	protected String name;
	protected String url;
	private String defaultConfig;

	protected CoreTemplate(String name, String url) {
		this.name = name;
		this.url = url;
		setupConfigurations();
	}

	protected abstract void setupConfigurations();
	
	public abstract SolrField[] getFieldValues();
	
	protected Map<String, IndexConfiguration> configurations = new HashMap<>();

	protected void addConfiguration(IndexConfiguration config) {
		this.configurations.put(config.getName(), config);
	}

	protected void setConfigAsDefault(String configName) {
		if(this.configurations.containsKey(configName))
			this.defaultConfig = configName;
		else throw new SearchConfigException("Cannot set configuration "+configName+" since it does not exist");
	}

	public IndexConfiguration getDefaultConfig() {
		if(this.defaultConfig != null)
			return this.configurations.get(defaultConfig);
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
