package org.nextprot.api.rdf.sparql;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nextprot.api.rdf.service.SparqlEndpoint;
import org.nextprot.api.rdf.service.SparqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:app-context.xml" })
@Ignore
public class SparqlServiceTest {

	String sSuperLite = "?entry :isoform/:expression/:term ?s." + "?s :subPartOf cv:TS-1030;rdfs:label ?name.";

	String sLite = "SELECT  ?entry { " + "?entry :isoform/:expression/:term ?s." + "?s :childOf cv:TS-1030;rdfs:label ?name. } order by ?entry LIMIT 5";

	String s = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "PREFIX : <http://nextprot.org/rdf#>" + "PREFIX cv: <http://nextprot.org/rdf/terminology/>" + "SELECT  ?entry { "
			+ "?entry :isoform/:expression/:term ?s." + "?s rdfs:subClassOf cv:TS-1030;rdfs:label ?name. } order by ?entry LIMIT 5";

	String c = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "PREFIX : <http://nextprot.org/rdf#>" + "PREFIX cv: <http://nextprot.org/rdf/terminology/>"
			+ "SELECT (count(distinct ?entry) as ?c) { " + "?entry :isoform/:expression/:term ?s." + "?s rdfs:subClassOf cv:TS-1030;rdfs:label ?name. } order by ?entry LIMIT 5";

	@Autowired
	private SparqlService sparqlService;

	@Autowired
	private SparqlEndpoint sparqlEndpoint;

	// TODO: THIS TEST IS USELESS
	@Test
	public void testEntries() {
		// List<String> entries = advanceQueryService.findEntries(SparqlEndpoint.Virtuoso, sLite, "title1");
		List<String> entries = sparqlService.findEntries(sLite, sparqlEndpoint.getUrl(), "title1");
		for (String s : entries) {
			//System.out.println(s);
		}
	}

	@Test
	public void testNoCacheEntries() {

		// List<String> entries = advanceQueryService.findEntries(SparqlEndpoint.Virtuoso, sLite, "title1");
		List<String> entries = sparqlService.findEntriesNoCache(sLite, sparqlEndpoint.getUrl(), "titleNoCache", "testId" + System.currentTimeMillis());
		for (String s : entries) {
			//System.out.println(s);
		}
	}

}
