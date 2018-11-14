package org.nextprot.api.solr.core.impl;


import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.nextprot.api.commons.exception.SearchConfigException;
import org.nextprot.api.solr.core.SolrCore;
import org.nextprot.api.solr.query.impl.config.AutocompleteConfiguration;
import org.nextprot.api.solr.query.impl.config.IndexConfiguration;
import org.nextprot.api.solr.query.impl.config.SortConfig;

import java.util.HashMap;
import java.util.Map;

public abstract class RealSolrCore implements SolrCore {

	protected String name;
	protected Alias alias;
	protected IndexConfiguration defaultConfiguration;
	protected SortConfig[] sortConfigurations;
	protected AutocompleteConfiguration autocompleteConfiguration;
	protected String defaultConfigName;
	protected Map<String, IndexConfiguration> configurations = new HashMap<>();

	protected RealSolrCore(String name, Alias alias) {
		this.name = name;
		this.alias = alias;
		sortConfigurations = newSortConfigurations();
		defaultConfiguration = newDefaultConfiguration();
		autocompleteConfiguration = newAutoCompleteConfiguration(defaultConfiguration);
		setupConfigurations();
	}

	protected void addConfiguration(IndexConfiguration config) {
		this.configurations.put(config.getName(), config);
	}

	protected void setConfigAsDefault(String configName) {
		if(this.configurations.containsKey(configName))
			this.defaultConfigName = configName;
		else throw new SearchConfigException("Cannot set configuration "+configName+" since it does not exist");
	}

	@Override
	public IndexConfiguration getDefaultConfig() {
		if(this.defaultConfigName != null)
			return this.configurations.get(defaultConfigName);
		else if(this.configurations.size() == 1)
			return this.configurations.entrySet().iterator().next().getValue();		// retrieve only element
		else throw new SearchConfigException("Default configuration has not been properly set for index "+name);
	}

	@Override
	public IndexConfiguration getConfig(String configName) {
		if (this.configurations.containsKey(configName))
			return this.configurations.get(configName);
		else
			throw new SearchConfigException("Configuration "+configName+" does not exist");
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Alias getAlias() {
		return alias;
	}

	@Override
	public String getUrl() {
		String solrUrl = getServerUrl();

		if (solrUrl.charAt(solrUrl.length()-1) != '/') {
			solrUrl = solrUrl + '/';
		}

		return solrUrl + getName();
	}

	@Override
	public SolrCoreServer newSolrServer() {

		return new SolrCoreServer(new HttpSolrServer(getUrl()));
	}

	/** @return the server base url */
	protected abstract String getServerUrl();

	protected abstract IndexConfiguration newDefaultConfiguration();
	protected abstract AutocompleteConfiguration newAutoCompleteConfiguration(IndexConfiguration defaultConfiguration);
	protected abstract SortConfig[] newSortConfigurations();
	protected abstract void setupConfigurations();

}
