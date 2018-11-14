package org.nextprot.api.solr.core.impl;

import org.junit.Assert;
import org.junit.Test;

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
	public void compareResultsFromCrickAndKant() {

		SolrCvCore cvCoreKant = new SolrCvCore("http://kant:8983/solr");
		SolrCvCore cvCoreCrick = new SolrCvCore("http://crick:8983/solr");

		SolrCoreHttpClient kantClient = cvCoreKant.newSolrClient();
		SolrCoreHttpClient crickClient = cvCoreCrick.newSolrClient();
/*
		kantClient.query();

		QueryRequest qr = new QueryRequest();
		qr.setQuery("11167787"); // some existing pubmed id
		qr.setQuality("");
		qr.setRows("50");
		qr.setSort("");
		qr.setMode(null);
		qr.setSparql(null);
		qr.setOrder("");
		qr.setFilter("");
		Query q = service.buildQueryForSearchIndexes( "publication", "simple",  qr);
		SearchResult result = service.executeQuery(q);
		*/
	}

}