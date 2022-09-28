package org.nextprot.api.rdf.service.impl;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.rdf.service.HttpSparqlService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.nextprot.api.rdf.service.HttpSparqlService.SparqlResponse.newRdfEntryConv;
import static org.nextprot.api.rdf.service.impl.HttpSparqlServiceImpl.SPARQL_DEFAULT_URL;


public class HttpSparqlServiceImplTest {

	@Test
	@Ignore
	public void testSearchWithOneVar() {
		
		HttpSparqlService sparqlService = new HttpSparqlServiceImpl();
		
		HttpSparqlService.SparqlResponse response = sparqlService.executeSparqlQuery(SPARQL_DEFAULT_URL,
				"select distinct ?entry where {\n" +
						"  ?entry :isoform ?iso.\n" +
						"  ?iso :keyword / :term cv:KW-0597.\n" +
						"  ?iso :cellularComponent /:term /:childOf  cv:SL-0086.\n" +
						"}");
		
		checkExpectedValues(response, Collections.singletonList("entry"), 5666);
	}

	@Test
	public void testNewRdfEntryConv() {

		HttpSparqlService.SparqlResponse response = new HttpSparqlService.SparqlResponse();
		response.addResult("entry", "http://nextprot.org/rdf/entry/NX_P36969");

		List<String> entryAccessions = response.mapResults("entry", newRdfEntryConv());
		Assert.assertEquals("NX_P36969", entryAccessions.get(0));
	}

	@Test
	@Ignore
	public void testSearchWithTwoVars() {
		
		HttpSparqlService sparqlService = new HttpSparqlServiceImpl();
		
		HttpSparqlService.SparqlResponse response = sparqlService.executeSparqlQuery(SPARQL_DEFAULT_URL, "select distinct ?entry ?iso where {\n" + "  ?entry :isoform ?iso.\n" + "  ?iso :keyword / :term cv:KW-0597.\n" + "  ?iso :cellularComponent /:term /:childOf cv:SL-0086.\n" + "}");
		
		checkExpectedValues(response, Arrays.asList("entry", "iso"), 13915);
	}

	private void checkExpectedValues(HttpSparqlService.SparqlResponse response, List<String> expectedVars, int expectedRows) {

		Assert.assertTrue(!response.getVars().isEmpty());
		Assert.assertEquals(expectedVars, response.getVars());
		Assert.assertTrue(response.rows() >= expectedRows);
	}
}