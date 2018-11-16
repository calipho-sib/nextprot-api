package org.nextprot.api.solr.core.impl.settings;

import org.nextprot.api.commons.exception.SearchConfigException;
import org.nextprot.api.solr.core.QuerySettings;
import org.nextprot.api.solr.core.SolrField;
import org.nextprot.api.solr.query.QueryConfiguration;
import org.nextprot.api.solr.query.QueryMode;

import java.util.HashMap;
import java.util.Map;

public abstract class QueryBaseSettings<F extends SolrField> implements QuerySettings<F> {

	private final Map<QueryMode, QueryConfiguration<F>> configurations = new HashMap<>();
	private final QueryMode defaultMode;

	public QueryBaseSettings() {

		defaultMode = setupConfigs(configurations);

		if (defaultMode == null) {
			throw new SearchConfigException("default configuration mode has to be defined");
		}

		if (!configurations.containsKey(defaultMode)) {
			throw new SearchConfigException("missing default configuration");
		}
	}

	@Override
	public QueryConfiguration<F> getConfig(QueryMode mode) {

		if (configurations.containsKey(mode)) {
			return configurations.get(mode);
		}
		throw new SearchConfigException("Configuration for mode "+mode+" does not exist");
	}

	@Override
	public QueryMode getDefaultMode() {

		return defaultMode;
	}

	/** setup configurations and return the default search mode */
	protected abstract QueryMode setupConfigs(Map<QueryMode, QueryConfiguration<F>> configurations);
}
