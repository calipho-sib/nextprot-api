package org.nextprot.api.solr.core.impl.component;

import org.nextprot.api.solr.core.QueryConfigurations;
import org.nextprot.api.solr.core.SolrCore;
import org.nextprot.api.solr.core.SolrField;
import org.nextprot.api.solr.core.SolrHttpClient;
import org.nextprot.api.solr.core.impl.SolrCvCore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class CvCore implements SolrCore {

	@Value("${solr.url}")
	private String solrServerBaseURL;

	private SolrCore core;

	@PostConstruct
	private void init() {
		core = new SolrCvCore(solrServerBaseURL);
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
	public QueryConfigurations getQueryConfigurations() {
		return core.getQueryConfigurations();
	}

	@Override
	public SolrHttpClient newSolrClient() {
		return core.newSolrClient();
	}
}
