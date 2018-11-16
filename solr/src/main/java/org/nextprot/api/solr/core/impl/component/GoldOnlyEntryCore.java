package org.nextprot.api.solr.core.impl.component;


import org.nextprot.api.solr.core.QuerySettings;
import org.nextprot.api.solr.core.SolrCore;
import org.nextprot.api.solr.core.SolrField;
import org.nextprot.api.solr.core.SolrHttpClient;
import org.nextprot.api.solr.core.impl.SolrGoldOnlyEntryCore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class GoldOnlyEntryCore implements SolrCore {

	@Value("${solr.url}")
	private String solrServerBaseURL;

	private SolrCore core;

	@PostConstruct
	private void init() {
		core = new SolrGoldOnlyEntryCore(solrServerBaseURL);
	}

	@Override
	public String getName() {
		return core.getName();
	}

	@Override
	public SolrCore.Alias getAlias() {
		return core.getAlias();
	}

	@Override
	public SolrField[] getSchema() {
		return core.getSchema();
	}

	@Override
	public QuerySettings getQuerySettings() {
		return core.getQuerySettings();
	}

	@Override
	public SolrHttpClient newSolrClient() {
		return core.newSolrClient();
	}
}