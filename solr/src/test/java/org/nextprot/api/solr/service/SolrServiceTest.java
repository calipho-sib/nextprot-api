package org.nextprot.api.solr.service;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.solr.core.Entity;
import org.nextprot.api.solr.query.Query;
import org.nextprot.api.solr.query.QueryMode;
import org.nextprot.api.solr.query.dto.QueryRequest;
import org.nextprot.api.solr.query.dto.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({"unit", "unit-schema-nextprot"})
@DirtiesContext
@ContextConfiguration({"classpath:spring/commons-context.xml"})
public class SolrServiceTest {

    @Autowired
    private SolrService service;

    @Test
    public void testSuggestionsAndCollations() throws Exception {
    	QueryRequest qr = new QueryRequest();
    	qr.setQuery("insulin phosphorilation intrcellular");
    	qr.setQuality("gold");
    	qr.setRows("50");
    	qr.setSort("");
    	qr.setOrder("");
    	qr.setFilter("");
    	Query q = service.buildQueryForSearchIndexes(Entity.Entry, QueryMode.SIMPLE,  qr);
		SearchResult result = service.executeQuery(q);
		long numFound = result.getFound();
		Set<Entry<String,List<String>>>suggestions = result.getSuggestions().entrySet();

		Set<Map<String, Object>> collations = result.getCollations();

		// we check that there is no hit found 
		Assert.assertEquals(0, numFound);
		// we check that we get suggestions
		Assert.assertTrue(suggestions.size()>0);
		// we check that we have collations
		Assert.assertTrue(collations.size()>0);
    }

    @Test
    public void testSearch3hydroanth_dOase_animal() throws Exception {
    	QueryRequest qr = new QueryRequest();
    	qr.setQuery("3hydroanth_dOase_animal");
    	qr.setQuality("gold");
    	qr.setRows("50");
    	qr.setSort("");
    	qr.setOrder("");
    	qr.setFilter("");
    	Query q = service.buildQueryForSearchIndexes( Entity.Entry, QueryMode.SIMPLE,  qr);
		SearchResult result = service.executeQuery(q);
		long numFound = result.getFound();
		Set<Entry<String,List<String>>>suggestions = result.getSuggestions().entrySet();

		Set<Map<String, Object>> collations = result.getCollations();

		// we check that there is no hit found 
		Assert.assertTrue(numFound==1);
		// we check that we get no suggestions
		Assert.assertTrue(suggestions.size()==0);
		// we check that we have no collations
		Assert.assertTrue(collations.size()==0);
    }

    @Test
    public void testGoColonIsEscaped() throws Exception {
    	QueryRequest qr = new QueryRequest();
    	qr.setQuery("go:0004386");
    	qr.setQuality("gold");
    	qr.setRows("50");
    	qr.setSort("");
    	qr.setOrder("");
    	qr.setFilter("");
    	Query q = service.buildQueryForSearchIndexes(Entity.Entry, QueryMode.SIMPLE,  qr);
    	//IndexConfiguration ic = this.configuration.getIndexByName("entry").getConfig("simple");
    	//SolrQuery sq = service.buildSolrIdQuery(q, ic);
    	SearchResult result = service.executeIdQuery(q);
		long numFound = result.getFound();
		Assert.assertTrue(numFound>=0); // we should get no error
    }

    @Test
    public void testAuthorFieldColonIsNotEscaped() throws Exception {
    	QueryRequest qr = new QueryRequest();
    	qr.setQuery("author:bairoch");
    	qr.setQuality("gold");
    	qr.setRows("50");
    	qr.setSort("");
    	qr.setOrder("");
    	qr.setFilter("");
    	Query q = service.buildQueryForSearchIndexes( Entity.Entry, QueryMode.SIMPLE,  qr);
    	SearchResult result = service.executeIdQuery(q);
		long numFound = result.getFound();
		Assert.assertTrue(numFound>=0); // we should get no error
    }
    
    @Test
    public void testPlusAreRemoved() throws Exception {
    	String s = "+insulin +phosphorylation +intracellular";
    	String s2 = StringUtils.removePlus(s);
    	Assert.assertEquals("insulin phosphorylation intracellular", s2);
    }

    
	@Test
	public void testGetFoundFacets() throws Exception {
		QueryRequest qr = new QueryRequest();
		qr.setQuery("krypton");
		qr.setQuality("gold");
		qr.setSort("");
		qr.setOrder("");
		Query q = service.buildQueryForSearchIndexes(Entity.Entry, QueryMode.SIMPLE, qr);
		SearchResult result = service.executeIdQuery(q);
		List<Map<String, Object>> found = result.getFoundFacets("id");
		Assert.assertEquals(result.getFound(), found.size());
	}
	
    @Test
    public void testPublicationSearchByPubmedId() throws Exception {
    	// {"filter": "", "quality": "gold", "query": "PEX19", "sparql": null, "sort": "", "order": "", "mode": null, "rows": 50}
    	QueryRequest qr = new QueryRequest();
    	qr.setQuery("11167787"); // some existing pubmed id
    	qr.setQuality("");
    	qr.setRows("50");
    	qr.setSort("");
    	qr.setMode(null);
    	qr.setSparql(null);
    	qr.setOrder("");
    	qr.setFilter("");
    	Query q = service.buildQueryForSearchIndexes( Entity.Publication, QueryMode.SIMPLE,  qr);
		SearchResult result = service.executeQuery(q);
		long numFound = result.getFound();
		// we check that there is 1 hit found
	    Assert.assertEquals(1, numFound);
//		Map<String,Object> doc = result.getResults().get(0);
//		for (String k: doc.keySet()) {
//			System.out.println("field:" + k);
//			System.out.println("class:" +doc.get(k).getClass().toString());
//			System.out.println("value:" + doc.get(k));
//			System.out.println("----------------------");
//		}
    }
	
	
}

