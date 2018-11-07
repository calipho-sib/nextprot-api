package org.nextprot.api.solr.query;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.nextprot.api.commons.utils.StringUtils;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({"unit", "unit-schema-nextprot"})
@DirtiesContext
@ContextConfiguration({"classpath:spring/commons-context.xml","classpath:spring/solr-context.xml"})
public class SolrQueryServiceTest {

    @Autowired
    private SolrQueryService service;

    private boolean debug = false;
    
    @Test
    public void testSuggestionsAndCollations() throws Exception {
    	QueryRequest qr = new QueryRequest();
    	qr.setQuery("insulin phosphorilation intrcellular");
    	qr.setQuality("gold");
    	qr.setRows("50");
    	qr.setSort("");
    	qr.setOrder("");
    	qr.setFilter("");
    	Query q = service.buildQueryForSearchIndexes("entry", "simple",  qr);
		SearchResult result = service.executeQuery(q);
		long numFound = result.getFound();
		if (debug) System.out.println("numFound="+numFound);
		Set<Entry<String,List<String>>>suggestions = result.getSuggestions().entrySet();
		for (Entry<String,List<String>> sug: suggestions) {
			for (String v: sug.getValue()) {
				if (debug) System.out.println("suggestion: " + sug.getKey() + " => " + v);
			}
		}
		Set<Map<String, Object>> collations = result.getCollations();
		for (Map<String, Object> col: collations) {
			if (debug) System.out.println("collation: q=" + col.get(SearchResult.Spellcheck.COLLATION_QUERY)
					+ ", hits=" + col.get(SearchResult.Spellcheck.COLLATION_HITS));
		}		
		// we check that there is no hit found 
		assertTrue(numFound==0);
		// we check that we get suggestions
		assertTrue(suggestions.size()>0);
		// we check that we have collations
		assertTrue(collations.size()>0);
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
    	Query q = service.buildQueryForSearchIndexes( "entry", "simple",  qr);
		SearchResult result = service.executeQuery(q);
		long numFound = result.getFound();
		if (debug) System.out.println("numFound="+numFound);
		Set<Entry<String,List<String>>>suggestions = result.getSuggestions().entrySet();
		for (Entry<String,List<String>> sug: suggestions) {
			for (String v: sug.getValue()) {
				if (debug) System.out.println("suggestion: " + sug.getKey() + " => " + v);
			}
		}
		Set<Map<String, Object>> collations = result.getCollations();
		for (Map<String, Object> col: collations) {
			if (debug) System.out.println("collation: q=" + col.get(SearchResult.Spellcheck.COLLATION_QUERY)
					+ ", hits=" + col.get(SearchResult.Spellcheck.COLLATION_HITS));
		}		
		// we check that there is no hit found 
		assertTrue(numFound==1);
		// we check that we get no suggestions
		assertTrue(suggestions.size()==0);
		// we check that we have no collations
		assertTrue(collations.size()==0);
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
    	Query q = service.buildQueryForSearchIndexes( "entry", "simple",  qr);
    	//IndexConfiguration ic = this.configuration.getIndexByName("entry").getConfig("simple");
    	//SolrQuery sq = service.buildSolrIdQuery(q, ic);
    	SearchResult result = service.executeIdQuery(q);
		long numFound = result.getFound();
		assertTrue(numFound>=0); // we should get no error
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
    	Query q = service.buildQueryForSearchIndexes( "entry", "simple",  qr);
    	SearchResult result = service.executeIdQuery(q);
		long numFound = result.getFound();
		assertTrue(numFound>=0); // we should get no error
    }
    
    @Test
    public void testPlusAreRemoved() throws Exception {
    	String s = "+insulin +phosphorylation +intracellular";
    	String s2 = StringUtils.removePlus(s);
    	if (debug) System.out.println(s);
    	if (debug) System.out.println(s2);
    	assertEquals("insulin phosphorylation intracellular", s2);
    }

    
	@Test
	public void testGetFoundFacets() throws Exception {
		QueryRequest qr = new QueryRequest();
		qr.setQuery("krypton");
		qr.setQuality("gold");
		qr.setSort("");
		qr.setOrder("");
		Query q = service.buildQueryForSearchIndexes("entry", "simple", qr);
		SearchResult result = service.executeIdQuery(q);
		List<Map<String, Object>> found = result.getFoundFacets("id");
		assertEquals(result.getFound(), found.size());
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
    	Query q = service.buildQueryForSearchIndexes( "publication", "simple",  qr);
		SearchResult result = service.executeQuery(q);
		long numFound = result.getFound();
		if (debug) System.out.println("numFound="+numFound);
		// we check that there is 1 hit found 
		assertTrue(numFound==1);
//		Map<String,Object> doc = result.getResults().get(0);
//		for (String k: doc.keySet()) {
//			System.out.println("field:" + k);
//			System.out.println("class:" +doc.get(k).getClass().toString());
//			System.out.println("value:" + doc.get(k));
//			System.out.println("----------------------");
//		}
    }
	
	
}

