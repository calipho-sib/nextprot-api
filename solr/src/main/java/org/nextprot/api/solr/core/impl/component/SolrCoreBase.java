package org.nextprot.api.solr.core.impl.component;


import org.nextprot.api.solr.core.SolrCore;
import org.nextprot.api.solr.core.impl.SolrCoreHttpClient;

public abstract class SolrCoreBase implements SolrCore {

	private final String name;
	private final String solrServerBaseURL;
	private final Alias alias;

	protected SolrCoreBase(String name, Alias alias, String solrServerBaseURL) {
		this.name = name;
		this.alias = alias;
		this.solrServerBaseURL = solrServerBaseURL;
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
}
