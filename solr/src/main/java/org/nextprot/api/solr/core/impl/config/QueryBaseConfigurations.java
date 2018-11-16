package org.nextprot.api.solr.core.impl.config;

import org.nextprot.api.commons.exception.SearchConfigException;
import org.nextprot.api.solr.core.QueryConfiguration;
import org.nextprot.api.solr.core.QueryConfigurations;
import org.nextprot.api.solr.core.SearchMode;

import java.util.HashMap;
import java.util.Map;

public abstract class QueryBaseConfigurations implements QueryConfigurations {

	private final Map<SearchMode, QueryConfiguration> configurations = new HashMap<>();
	private final SearchMode defaultMode;

	public QueryBaseConfigurations() {

		defaultMode = setupConfigs(configurations);

		if (defaultMode == null) {
			throw new SearchConfigException("default configuration mode has to be defined");
		}

		if (!configurations.containsKey(defaultMode)) {
			throw new SearchConfigException("missing default configuration");
		}
	}

	@Override
	public QueryConfiguration getConfig(SearchMode mode) {

		if (configurations.containsKey(mode)) {
			return configurations.get(mode);
		}
		throw new SearchConfigException("Configuration for mode "+mode+" does not exist");
	}

	@Override
	public QueryConfiguration getDefaultConfig() {

		return configurations.get(defaultMode);
	}

	/** setup configurations and return the default search mode */
	protected abstract SearchMode setupConfigs(Map<SearchMode, QueryConfiguration> configurations);
}
