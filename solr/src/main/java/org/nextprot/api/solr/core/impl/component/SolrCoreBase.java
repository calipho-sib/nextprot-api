package org.nextprot.api.solr.core.impl.component;


import org.nextprot.api.commons.exception.SearchConfigException;
import org.nextprot.api.solr.core.SolrCore;
import org.nextprot.api.solr.core.impl.SolrCoreHttpClient;
import org.nextprot.api.solr.query.impl.config.AutocompleteConfiguration;
import org.nextprot.api.solr.query.impl.config.IndexConfiguration;
import org.nextprot.api.solr.query.impl.config.Mode;
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
	protected Mode defaultConfigMode;
	protected Map<Mode, IndexConfiguration> configurations = new HashMap<>();

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
		this.configurations.put(config.getMode(), config);
	}

	protected void setConfigAsDefault(Mode mode) {
		if(this.configurations.containsKey(mode))
			this.defaultConfigMode = mode;
		else throw new SearchConfigException("Cannot set configuration "+mode+" since it does not exist");
	}

	@Override
	public IndexConfiguration getDefaultConfig() {
		if(this.defaultConfigMode != null)
			return this.configurations.get(defaultConfigMode);
		else if(this.configurations.size() == 1)
			return this.configurations.entrySet().iterator().next().getValue();		// retrieve only element
		else throw new SearchConfigException("Default configuration has not been properly set for index "+name);
	}

	@Override
	public IndexConfiguration getConfig(Mode mode) {
		if (this.configurations.containsKey(mode))
			return this.configurations.get(mode);
		else
			throw new SearchConfigException("Configuration "+mode+" does not exist");
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

		return new SolrCoreHttpClient(name, solrServerBaseURL);
	}

	protected abstract IndexConfiguration newDefaultConfiguration();
	protected abstract AutocompleteConfiguration newAutoCompleteConfiguration(IndexConfiguration defaultConfiguration);
	protected abstract SortConfig[] newSortConfigurations();
	protected abstract void setupConfigurations();

}
