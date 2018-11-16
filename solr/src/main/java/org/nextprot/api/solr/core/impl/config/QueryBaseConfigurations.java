package org.nextprot.api.solr.core.impl.config;

import org.nextprot.api.commons.exception.SearchConfigException;
import org.nextprot.api.solr.core.QueryConfiguration;
import org.nextprot.api.solr.core.QueryConfigurations;
import org.nextprot.api.solr.core.SearchMode;
import org.nextprot.api.solr.core.SolrField;

import java.util.HashMap;
import java.util.Map;

public abstract class QueryBaseConfigurations<F extends SolrField> implements QueryConfigurations<F> {

	private final Map<SearchMode, QueryConfiguration<F>> configurations = new HashMap<>();
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
	public QueryConfiguration<F> getConfig(SearchMode mode) {

		if (configurations.containsKey(mode)) {
			return configurations.get(mode);
		}
		throw new SearchConfigException("Configuration for mode "+mode+" does not exist");
	}

	@Override
	public SearchMode getDefaultMode() {

		return defaultMode;
	}

	/** setup configurations and return the default search mode */
	protected abstract SearchMode setupConfigs(Map<SearchMode, QueryConfiguration<F>> configurations);
}
