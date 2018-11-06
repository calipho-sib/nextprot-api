package org.nextprot.api.web.service.impl;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.solr.SolrService;
import org.nextprot.api.solr.dto.Query;
import org.nextprot.api.solr.dto.QueryRequest;
import org.nextprot.api.solr.dto.SearchResult;
import org.nextprot.api.web.dbunit.base.mvc.WebUnitBaseTest;
import org.nextprot.api.web.service.QueryBuilderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@ActiveProfiles({"dev","cache"})
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

    @Test
    public void shouldReturnResultsFromSimpleEntryQuery() throws Exception {

        QueryRequest qr = new QueryRequest();
        qr.setQuery("MSH6");

        Query q = queryBuilderService.buildQueryForSearch(qr, "entry");
        SearchResult result = service.executeQuery(q);

        assertTrue(result.getFound() > 100);
    }

    @Test
    public void shouldReturnResultsFromSimpleGoldEntryQuery() throws Exception {

        QueryRequest qr = new QueryRequest();
        qr.setQuality("GOLD");
        qr.setQuery("MSH6");

        Query q = queryBuilderService.buildQueryForSearch(qr, "entry");
        SearchResult result = service.executeQuery(q);

        assertTrue(result.getFound()>70);
    }

    // indexes: gold-entry, entry, term, publication

    @Test
    public void shouldReturnResultsFromSimplePublicationQuery() throws Exception {

        QueryRequest qr = new QueryRequest();
        qr.setQuery("author:Doolittle");

        Query q = queryBuilderService.buildQueryForSearch(qr, "publication");
        SearchResult result = service.executeQuery(q);

        assertTrue(result.getFound()>20);
    }

    @Test
    public void shouldReturnResultsFromSimpleTermQuery() throws Exception {

        QueryRequest qr = new QueryRequest();
        qr.setQuery("liver");

        Query q = queryBuilderService.buildQueryForSearch(qr, "term");
        SearchResult result = service.executeQuery(q);

        Assert.assertTrue(result.getFound() > 1000);
    }

    @Test
    public void shouldReturnResultsFromSparqlQuery() throws Exception {

        QueryRequest qr = new QueryRequest();
        qr.setMode("advanced");
        qr.setQuality("gold");
        qr.setSparqlEngine("Jena");
        qr.setSparql("#Proteins phosphorylated and located in the cytoplasm\nselect distinct ?entry where {\n  ?entry :isoform ?iso.\n  ?iso :keyword / :term cv:KW-0597.\n  ?iso :cellularComponent /:term /:childOf cv:SL-0086.\n}");

        Query q = queryBuilderService.buildQueryForSearch(qr, "entry");
        SearchResult result = service.executeQuery(q);

        assertTrue(result.getFound() >= 5636);
    }

    @Ignore
    // TODO: UserQueryServiceImpl.getUserQueryByPublicId(id) could not find id from UserQueryServiceImpl.getNxqTutorialQueries() in miniwatt
    // Explanation: SparqlQueryDictionary was not able to find the .rq files via getSparqlQueryList() method (because of classpath ???)
    @Test
    public void shouldReturnResultsFromAdvancedQueryId() throws Exception {

        QueryRequest qr = new QueryRequest();
        qr.setMode("advanced");
        qr.setQuality("gold");
        qr.setSparqlEngine("Jena");
        qr.setQueryId("NXQ_00001");

        Query q = queryBuilderService.buildQueryForSearch(qr, "entry");
        SearchResult result = service.executeQuery(q);

        assertEquals(5618, result.getFound());
    }

    @Test
    public void shouldReturnResultsFromSharedProteinList() throws Exception {

        QueryRequest qr = new QueryRequest();
        qr.setQuality("gold");
        qr.setListId("Y7JPIEVH");
        qr.setListOwner("Guest");

        Query q = queryBuilderService.buildQueryForSearch(qr, "entry");
        SearchResult result = service.executeQuery(q);

        assertEquals(1, result.getFound());
    }

    @Test
    public void shouldReturnResultsFromSharedQueryList() throws Exception {

        QueryRequest qr = new QueryRequest();
        qr.setQuality("gold");
        qr.setMode("advanced");
        qr.setQueryId("3K8W9PJT");

        Query q = queryBuilderService.buildQueryForSearch(qr, "entry");
        SearchResult result = service.executeQuery(q);

        assertTrue(result.getFound() >= 5636);
    }
}