package org.nextprot.api.solr;


import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import org.nextprot.api.commons.utils.Pair;
import org.nextprot.api.commons.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({"unit", "unit-schema-nextprot"})
@DirtiesContext
@ContextConfiguration({"classpath:spring/commons-context.xml","classpath:spring/solr-context.xml"})

public class SolrServiceTest  {

    @Autowired
    private SolrService service;
    
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
		long numFound = result.getNumFound();
		if (debug) System.out.println("numFound="+numFound);
		Set<Entry<String,List<String>>>suggestions = result.getSpellcheck().getSuggestions().entrySet();
		for (Entry<String,List<String>> sug: suggestions) {
			for (String v: sug.getValue()) {
				if (debug) System.out.println("suggestion: " + sug.getKey() + " => " + v);
			}
		}
		Set<Pair<String,Long> >collations = result.getSpellcheck().getCollations();
		for (Pair<String,Long> col: collations) {
			if (debug) System.out.println("collation: q=" + col.getFirst() + ", hits=" + col.getSecond());
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
		long numFound = result.getNumFound();
		if (debug) System.out.println("numFound="+numFound);
		Set<Entry<String,List<String>>>suggestions = result.getSpellcheck().getSuggestions().entrySet();
		for (Entry<String,List<String>> sug: suggestions) {
			for (String v: sug.getValue()) {
				if (debug) System.out.println("suggestion: " + sug.getKey() + " => " + v);
			}
		}
		Set<Pair<String,Long> >collations = result.getSpellcheck().getCollations();
		for (Pair<String,Long> col: collations) {
			if (debug) System.out.println("collation: q=" + col.getFirst() + ", hits=" + col.getSecond());
		}		
		// we check that there is no hit found 
		assertTrue(numFound==1);
		// we check that we get no suggestions
		assertTrue(suggestions.size()==0);
		// we check that we have no collations
		assertTrue(collations.size()==0);
    }

    @Test
    public void testPlusAreRemoved() throws Exception {
    	String s = "+insulin +phosphorylation +intracellular";
    	String s2 = StringUtils.removePlus(s);
    	if (debug) System.out.println(s);
    	if (debug) System.out.println(s2);
    	assertEquals("insulin phosphorylation intracellular", s2);
    }
 
    
    
}