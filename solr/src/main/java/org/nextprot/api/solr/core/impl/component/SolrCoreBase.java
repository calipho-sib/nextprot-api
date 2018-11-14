package org.nextprot.api.solr.core.impl.component;


import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.nextprot.api.commons.exception.SearchConfigException;
import org.nextprot.api.solr.core.SolrCore;
import org.nextprot.api.solr.core.impl.SolrCoreHttpClient;
import org.nextprot.api.solr.query.impl.config.AutocompleteConfiguration;
import org.nextprot.api.solr.query.impl.config.IndexConfiguration;
import org.nextprot.api.solr.query.impl.config.SortConfig;

import java.util.HashMap;
import java.util.Map;

public abstract class SolrCoreBase implements SolrCore {

	protected String name;
	private String solrServerBaseURL;
	protected Alias alias;
	protected IndexConfiguration defaultConfiguration;
	protected SortConfig[] sortConfigurations;
	protected AutocompleteConfiguration autocompleteConfiguration;
	protected String defaultConfigName;
	protected Map<String, IndexConfiguration> configurations = new HashMap<>();

	protected SolrCoreBase(String name, Alias alias, String solrServerBaseURL) {
		this.name = name;
		this.alias = alias;
		this.solrServerBaseURL = solrServerBaseURL;
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
	public SolrCoreHttpClient newSolrClient() {

		return new SolrCoreHttpClient(name, new HttpSolrServer(solrServerBaseURL));
	}

	protected abstract IndexConfiguration newDefaultConfiguration();
	protected abstract AutocompleteConfiguration newAutoCompleteConfiguration(IndexConfiguration defaultConfiguration);
	protected abstract SortConfig[] newSortConfigurations();
	protected abstract void setupConfigurations();

}
