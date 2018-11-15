package org.nextprot.api.solr.core.impl;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.solr.query.Query;

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

	/*

	simple
	autocomplete
	pl_search for protein list
	 */


	@Test
	public void compareResultsFromCrickAndKant() {

		SolrCvCore cvCoreKant = new SolrCvCore("http://kant:8983/solr");
		SolrCvCore cvCoreCrick = new SolrCvCore("http://crick:8983/solr");

		SolrCoreHttpClient kantClient = cvCoreKant.newSolrClient();
		SolrCoreHttpClient crickClient = cvCoreCrick.newSolrClient();

		Query query = new Query(cvCoreKant);
		query.addQuery("protein");

		//kantClient.query();

		//QueryRequest qr = new QueryRequest();
		//qr.setQuality("gold");
		//qr.setRows("5");

		//kantClient.query();

		//Query q = queryBuilderService.buildQueryForSearch(qr, "entry");
		//try {
		//	SearchResult searchResult = solrQueryService.executeQuery(q);

		/*
		Complicated to make queries:

		1. build query
		2.


		 */

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