package org.nextprot.api.solr.core.impl;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.exception.SearchConnectionException;
import org.nextprot.api.solr.core.QueryConfiguration;
import org.nextprot.api.solr.core.SolrCore;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.nextprot.api.solr.query.Query;
import org.nextprot.api.solr.query.QueryExecutor;
import org.nextprot.api.solr.query.dto.SearchResult;

public class SolrCvCoreTest {

	@Test
	public void testSolrOnKant() {

		SolrCvCore cvCore = new SolrCvCore("http://kant:8983/solr");
		SolrCoreHttpClient client = cvCore.newSolrClient();
		Assert.assertEquals("http://kant:8983/solr/npcvs1", client.getURL());
	}

	@Test
	public void testSolrOnCrick() {

		SolrCvCore cvCore = new SolrCvCore("http://crick:8983/solr");
		SolrCoreHttpClient client = cvCore.newSolrClient();
		Assert.assertEquals("http://crick:8983/solr/npcvs1", client.getURL());
	}

	@Test
	public void compareResultsFromCrickAndKant() throws QueryConfiguration.MissingSortConfigException {

		SolrCore<EntrySolrField> cvCoreBuild = new SolrGoldOnlyEntryCore("http://kant:8983/solr");
		SolrCore<EntrySolrField> cvCoreAlpha = new SolrGoldOnlyEntryCore("http://uat-web2:8983/solr");

		Query<EntrySolrField> queryBuild = new Query<>(cvCoreBuild).rows(50).addQuery("MSH6");
		Query<EntrySolrField> queryAlpha = new Query<>(cvCoreAlpha).rows(50).addQuery("MSH6");

		SearchResult bResult = executeQuery(queryBuild);
		SearchResult aResult = executeQuery(queryAlpha);

		Assert.assertEquals(aResult.getResults(), bResult.getResults());

		Assert.assertEquals(aResult, bResult);
	}

	private SearchResult executeQuery(Query query) throws QueryConfiguration.MissingSortConfigException {

		SolrCore core = query.getSolrCore();

		QueryExecutor executor = new QueryExecutor(core);

		try {
			return executor.execute(query.getQueryConfiguration().convertQuery(query));
		} catch (SolrServerException e) {
			throw new SearchConnectionException("Could not connect to Solr server. Please contact support or try again later.");
		}
	}
}