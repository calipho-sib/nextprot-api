package org.nextprot.api.solr;

import java.util.HashMap;
import java.util.Map;

import org.nextprot.api.exceptions.SearchConfigException;
import org.nextprot.api.solr.config.IndexConfiguration;

public abstract class AbstractSolrIndex implements SolrIndex {

	protected String name;
	protected String url;
	protected String defaultConfig;
	
	protected AbstractSolrIndex(String name, String url) {
		this.name = name;
		this.url = url;
	}
	
	protected Map<String, IndexConfiguration> configurations = new HashMap<String, IndexConfiguration>();
	
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
	
	public static class FieldValue {
		private String name;
		
		private FieldValue(String name) {
			this.name = name;
		}
		
		public static FieldValue create(String name) {
			return new FieldValue(name);
		}
		
		public String getName() {
			return this.name;
		}
	}
	
}
