package org.nextprot.api.web.service.impl;

import org.junit.Test;
import org.nextprot.api.solr.Query;
import org.nextprot.api.solr.QueryRequest;
import org.nextprot.api.solr.SearchResult;
import org.nextprot.api.solr.SolrService;
import org.nextprot.api.web.dbunit.base.mvc.WebUnitBaseTest;
import org.nextprot.api.web.service.QueryBuilderService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SolrServiceTest extends WebUnitBaseTest {

    @Autowired private SolrService service;
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

    @Test
    public void shouldReturnSomeResultsFromAccessionSet() throws Exception {

	    Set<String> accessions = new HashSet<>(Arrays.asList("NX_P02671", "NX_P02675", "NX_P02679"));

        QueryRequest qr = new QueryRequest();
        qr.setQuality("GOLD");
        qr.setEntryAccessionSet(accessions);

        Query q = queryBuilderService.buildQueryForSearch(qr, "entry");
        SearchResult result = service.executeQuery(q);

        assertEquals(3, result.getFound());

        for (Map<String, Object> resultMap : result.getResults()) {

            assertTrue(accessions.contains(resultMap.get("id")));
        }
    }

    @Test
    public void shouldReturnEmptyResultsFromEmptyAccessionSet() throws Exception {

        QueryRequest qr = new QueryRequest();
        qr.setQuality("GOLD");
        qr.setEntryAccessionSet(new HashSet<>());

        Query q = queryBuilderService.buildQueryForSearch(qr, "entry");
        SearchResult result = service.executeQuery(q);

        assertEquals(0, result.getFound());
    }

    @Test
    public void shouldReturnEmptyResultsFromEmptyQuery() throws Exception {

        QueryRequest qr = new QueryRequest();

        Query q = queryBuilderService.buildQueryForSearch(qr, "entry");
        SearchResult result = service.executeQuery(q);

        assertEquals(0, result.getFound());
    }
}