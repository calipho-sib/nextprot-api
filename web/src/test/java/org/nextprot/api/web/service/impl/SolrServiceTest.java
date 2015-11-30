package org.nextprot.api.web.service.impl;

import org.junit.Test;
import org.nextprot.api.solr.*;
import org.nextprot.api.web.dbunit.base.mvc.WebUnitBaseTest;
import org.nextprot.api.web.service.QueryBuilderService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertTrue;

public class SolrServiceTest extends WebUnitBaseTest {

    @Autowired private SolrService service;
	@Autowired private SolrConfiguration configuration;
	@Autowired private QueryBuilderService queryBuilderService;

	@Test
	public void shoulReturnSomePublicationsWhenStopWordsAreIncludedInQuery() throws Exception {
    	QueryRequest qr;
    	Query q;
    	SearchResult result;
    	long numFound;
    	
    	qr = new QueryRequest();
    	qr.setQuery("polo like");
    	q = queryBuilderService.buildQueryForSearch(qr, "publication");
    	result = service.executeQuery(q);
		numFound = result.getFound();
		
		// we should get some results
		assertTrue(numFound>0); 

		qr = new QueryRequest();
    	qr.setQuery("polo like in the of");
    	q = queryBuilderService.buildQueryForSearch(qr, "publication");
    	result = service.executeQuery(q);
		numFound = result.getFound();
		
		// we should ALSO get some results
		assertTrue(numFound>0); 
    }

	
	@Test
	public void shoulReturnSomeEntriesWhenStopWordsAreIncludedInQuery() throws Exception {
    	QueryRequest qr;
    	Query q;
    	SearchResult result;
    	long numFound;
    	
    	qr = new QueryRequest();
    	qr.setQuery("insulin");
    	q = queryBuilderService.buildQueryForSearch(qr, "entry");
    	result = service.executeQuery(q);
		numFound = result.getFound();
		
		// we should get some results
		assertTrue(numFound>0); 

		qr = new QueryRequest();
    	qr.setQuery("insulin in the of");
    	q = queryBuilderService.buildQueryForSearch(qr, "entry");
    	result = service.executeQuery(q);
		numFound = result.getFound();
		
		// we should ALSO get some results
		assertTrue(numFound>0); 
    }
	
	@Test
	public void shoulReturnSomeTermsWhenStopWordsAreIncludedInQuery() throws Exception {
    	QueryRequest qr;
    	Query q;
    	SearchResult result;
    	long numFound;
    	
    	qr = new QueryRequest();
    	qr.setQuery("brain");
    	q = queryBuilderService.buildQueryForSearch(qr, "term");
    	result = service.executeQuery(q);
		numFound = result.getFound();
		
		// we should get some results
		assertTrue(numFound>0); 

		qr = new QueryRequest();
    	qr.setQuery("brain in the of");
    	q = queryBuilderService.buildQueryForSearch(qr, "term");
    	result = service.executeQuery(q);
		numFound = result.getFound();
		
		// we should ALSO get some results
		assertTrue(numFound>0); 
    }


}