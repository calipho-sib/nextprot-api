package org.nextprot.api.solr.core.impl.component;


import org.nextprot.api.solr.core.QuerySettings;
import org.nextprot.api.solr.core.SolrCore;
import org.nextprot.api.solr.core.SolrField;
import org.nextprot.api.solr.core.impl.SolrCoreHttpClient;

import java.util.Set;

public abstract class SolrCoreBase<F extends SolrField> implements SolrCore<F> {

	private final String name;
	private final String solrServerBaseURL;
	private final Alias alias;
	private final QuerySettings<F> settings;

	protected SolrCoreBase(String name, Alias alias, String solrServerBaseURL, Set<F> specificFieldSet) {
		this.name = name;
		this.alias = alias;
		this.solrServerBaseURL = solrServerBaseURL;
		this.settings = buildSettings(specificFieldSet);
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

	@Override
	public QuerySettings<F> getQuerySettings() {

		return settings;
	}

	protected abstract QuerySettings<F> buildSettings(Set<F> specificFieldSet);
}
