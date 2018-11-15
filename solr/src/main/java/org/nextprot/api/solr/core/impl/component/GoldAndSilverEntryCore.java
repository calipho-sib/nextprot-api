package org.nextprot.api.solr.core.impl.component;

import org.nextprot.api.solr.core.SolrCore;
import org.nextprot.api.solr.core.SolrField;
import org.nextprot.api.solr.core.SolrHttpClient;
import org.nextprot.api.solr.core.impl.SolrGoldAndSilverEntryCore;
import org.nextprot.api.solr.query.impl.config.IndexConfiguration;
import org.nextprot.api.solr.query.impl.config.Mode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class GoldAndSilverEntryCore implements SolrCore {

	@Value("${solr.url}")
	private String solrServerBaseURL;

	private SolrCore core;

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
	public SolrField[] getSchema() {
		return core.getSchema();
	}

	@Override
	public IndexConfiguration getDefaultConfig() {
		return core.getDefaultConfig();
	}

	@Override
	public IndexConfiguration getConfig(Mode mode) {
		return core.getConfig(mode);
	}

	@Override
	public SolrHttpClient newSolrClient() {
		return core.newSolrClient();
	}
}
