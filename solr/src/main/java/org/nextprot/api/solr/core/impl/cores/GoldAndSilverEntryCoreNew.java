package org.nextprot.api.solr.core.impl.cores;

import org.nextprot.api.solr.core.SolrField;
import org.nextprot.api.solr.query.impl.config.IndexConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class GoldAndSilverEntryCoreNew implements SolrCoreNew {

	@Value("${solr.url}")
	private String solrServerUrl;

	private GoldAndSilverEntryCoreImpl core;

	@PostConstruct
	private void init() {
		core = new GoldAndSilverEntryCoreImpl(solrServerUrl);
	}

	@Override
	public String getName() {
		return core.getName();
	}

	@Override
	public Alias getAlias() {
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
	public IndexConfiguration getConfig(String configName) {
		return core.getConfig(configName);
	}

	@Override
	public SolrServerNew newSolrServer() {
		return core.newSolrServer();
	}
}
