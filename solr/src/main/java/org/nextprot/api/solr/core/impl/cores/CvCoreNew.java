package org.nextprot.api.solr.core.impl.cores;

import org.nextprot.api.solr.core.SolrField;
import org.nextprot.api.solr.query.impl.config.IndexConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class CvCoreNew implements SolrCoreNew {

	@Value("${solr.url}")
	private String solrServerUrl;

	private CvCoreImpl core;

	@PostConstruct
	private void init() {
		core = new CvCoreImpl(solrServerUrl);
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
	public SolrCoreServerNew newSolrServer() {
		return core.newSolrServer();
	}
}
