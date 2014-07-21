package org.nextprot.api.sparql;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nextprot.api.service.SparqlEndpoint;
import org.nextprot.api.service.SparqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:app-context.xml" })
public class SparqlServiceTest {

	String sSuperLite = "?entry :isoform/:expression/:in ?s." + "?s :subPartOf term:TS-1030;rdfs:label ?name.";

	String sLite = "SELECT  ?entry { " + "?entry :isoform/:expression/:in ?s." + "?s :childOf term:TS-1030;rdfs:label ?name. } order by ?entry LIMIT 5";

	String s = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "PREFIX : <http://nextprot.org/rdf#>" + "PREFIX term: <http://nextprot.org/rdf/terminology/>" + "SELECT  ?entry { "
			+ "?entry :isoform/:expression/:in ?s." + "?s rdfs:subClassOf term:TS-1030;rdfs:label ?name. } order by ?entry LIMIT 5";

	String c = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "PREFIX : <http://nextprot.org/rdf#>" + "PREFIX term: <http://nextprot.org/rdf/terminology/>"
			+ "SELECT (count(distinct ?entry) as ?c) { " + "?entry :isoform/:expression/:in ?s." + "?s rdfs:subClassOf term:TS-1030;rdfs:label ?name. } order by ?entry LIMIT 5";

	@Autowired
	private SparqlService sparqlService;

	@Autowired
	private SparqlEndpoint sparqlEndpoint;

	@Test
	public void testEntries() {
		// List<String> entries = advanceQueryService.findEntries(SparqlEndpoint.Virtuoso, sLite, "title1");
		List<String> entries = sparqlService.findEntries(sLite, sparqlEndpoint.getUrl(), "title1");
		for (String s : entries) {
			System.out.println(s);
		}
	}

	@Test
	public void testNoCacheEntries() {
		System.out.println("Going to " + sparqlEndpoint.getUrl());
		// List<String> entries = advanceQueryService.findEntries(SparqlEndpoint.Virtuoso, sLite, "title1");
		List<String> entries = sparqlService.findEntriesNoCache(sLite, sparqlEndpoint.getUrl(), "titleNoCache", "testId" + System.currentTimeMillis());
		for (String s : entries) {
			System.out.println(s);
		}
	}

}
