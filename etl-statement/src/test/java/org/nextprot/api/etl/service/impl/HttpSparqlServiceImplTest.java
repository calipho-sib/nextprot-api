package org.nextprot.api.etl.service.impl;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.etl.service.HttpSparqlService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.nextprot.api.etl.service.impl.HttpSparqlServiceImpl.*;

public class HttpSparqlServiceImplTest {

	@Test
	public void testSearchWithOneVar() {

		HttpSparqlService sparqlService = new HttpSparqlServiceImpl();

		Map<String, Object> results = sparqlService.executeSparqlQuery(SPARQL_DEFAULT_URL,
				"select distinct ?entry where {\n" +
				"  ?entry :isoform ?iso.\n" +
				"  ?iso :keyword / :term cv:KW-0597.\n" +
				"  ?iso :cellularComponent /:term /:childOf cv:SL-0086.\n" +
				"}");

		checkExpectedValues(results, Collections.singletonList("entry"), 5666);
	}

	@Test
	public void testSearchWithTwoVars() {

		HttpSparqlService sparqlService = new HttpSparqlServiceImpl();

		Map<String, Object> results = sparqlService.executeSparqlQuery(SPARQL_DEFAULT_URL,
				"select distinct ?entry ?iso where {\n" +
						"  ?entry :isoform ?iso.\n" +
						"  ?iso :keyword / :term cv:KW-0597.\n" +
						"  ?iso :cellularComponent /:term /:childOf cv:SL-0086.\n" +
						"}");

		checkExpectedValues(results, Arrays.asList("entry", "iso"), 13915);
	}

	private void checkExpectedValues(Map<String, Object> results, List<String> expectedVars, int expectedRows) {

		Assert.assertTrue(results.containsKey(VARS));
		Assert.assertEquals(expectedVars, results.get(VARS));
		Assert.assertTrue(results.containsKey(ROWS));
		Assert.assertEquals(expectedRows, (int)results.get(ROWS));
		Assert.assertTrue(results.containsKey(RESULTS));
		Assert.assertEquals(expectedVars.size(), ((Map<String, Object>)results.get(RESULTS)).keySet().size());
	}
}