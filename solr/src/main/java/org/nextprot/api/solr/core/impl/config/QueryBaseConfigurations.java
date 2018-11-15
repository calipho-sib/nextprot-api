package org.nextprot.api.solr.core.impl.config;

import org.nextprot.api.commons.exception.SearchConfigException;
import org.nextprot.api.solr.core.QueryConfiguration;
import org.nextprot.api.solr.core.QueryConfigurations;
import org.nextprot.api.solr.core.SearchMode;

import java.util.HashMap;
import java.util.Map;

public abstract class QueryBaseConfigurations implements QueryConfigurations {

	protected IndexConfiguration defaultConfiguration;
	protected SortConfig[] sortConfigurations;
	protected AutocompleteConfiguration autocompleteConfiguration;
	protected SearchMode defaultConfigMode;
	protected Map<SearchMode, QueryConfiguration> configurations = new HashMap<>();

	public QueryBaseConfigurations() {

		sortConfigurations = newSortConfigurations();
		defaultConfiguration = newDefaultConfiguration();
		autocompleteConfiguration = newAutoCompleteConfiguration(defaultConfiguration);
		setupConfigurations();
	}

	protected void addConfiguration(QueryConfiguration config) {
		this.configurations.put(config.getMode(), config);
	}

	protected void setConfigAsDefault(SearchMode mode) {
		if(this.configurations.containsKey(mode))
			this.defaultConfigMode = mode;
		else throw new SearchConfigException("Cannot set configuration "+mode+" since it does not exist");
	}

	public SearchMode getDefaultMode() {

		if (this.defaultConfigMode != null) {
			return defaultConfigMode;
		}
		else if(this.configurations.size() == 1) {
			return this.configurations.keySet().iterator().next();
		}
		throw new SearchConfigException("Default configuration has not been properly set");
	}

	@Override
	public QueryConfiguration getDefaultConfig() {

		return this.configurations.get(getDefaultMode());
	}

	@Override
	public QueryConfiguration getConfig(SearchMode mode) {
		if (this.configurations.containsKey(mode))
			return this.configurations.get(mode);
		else
			throw new SearchConfigException("Configuration "+mode+" does not exist");
	}

	protected abstract IndexConfiguration newDefaultConfiguration();
	protected abstract AutocompleteConfiguration newAutoCompleteConfiguration(IndexConfiguration defaultConfiguration);
	protected abstract SortConfig[] newSortConfigurations();
	protected abstract void setupConfigurations();
}
