package org.nextprot.api.solr.core.impl.component;

import org.nextprot.api.solr.core.QuerySettings;
import org.nextprot.api.solr.core.SolrCore;
import org.nextprot.api.solr.core.SolrHttpClient;
import org.nextprot.api.solr.core.impl.SolrGoldAndSilverEntryCore;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class GoldAndSilverEntryCore implements SolrCore<EntrySolrField> {

	@Value("${solr.url}")
	private String solrServerBaseURL;

	private SolrCore<EntrySolrField> core;

	@PostConstruct
	private void init() {
		core = new SolrGoldAndSilverEntryCore(solrServerBaseURL);
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
	public EntrySolrField[] getSchema() {
		return core.getSchema();
	}

	@Override
	public QuerySettings<EntrySolrField> getQuerySettings() {
		return core.getQuerySettings();
	}

	@Override
	public SolrHttpClient newSolrClient() {
		return core.newSolrClient();
	}
}
