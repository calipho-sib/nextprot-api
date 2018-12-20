package org.nextprot.api.etl.service.impl;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.etl.service.HttpSparqlService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.nextprot.api.etl.service.HttpSparqlService.SparqlResponse;
import static org.nextprot.api.etl.service.impl.HttpSparqlServiceImpl.SPARQL_DEFAULT_URL;

public class HttpSparqlServiceImplTest {

	@Test
	public void testSearchWithOneVar() {

		HttpSparqlService sparqlService = new HttpSparqlServiceImpl();

		SparqlResponse response = sparqlService.executeSparqlQuery(SPARQL_DEFAULT_URL,
				"select distinct ?entry where {\n" +
				"  ?entry :isoform ?iso.\n" +
				"  ?iso :keyword / :term cv:KW-0597.\n" +
				"  ?iso :cellularComponent /:term /:childOf cv:SL-0086.\n" +
				"}");

		checkExpectedValues(response, Collections.singletonList("entry"), 5666);
	}

	@Test
	public void testSearchWithTwoVars() {

		HttpSparqlService sparqlService = new HttpSparqlServiceImpl();

		SparqlResponse response = sparqlService.executeSparqlQuery(SPARQL_DEFAULT_URL,
				"select distinct ?entry ?iso where {\n" +
						"  ?entry :isoform ?iso.\n" +
						"  ?iso :keyword / :term cv:KW-0597.\n" +
						"  ?iso :cellularComponent /:term /:childOf cv:SL-0086.\n" +
						"}");

		checkExpectedValues(response, Arrays.asList("entry", "iso"), 13915);
	}

	private void checkExpectedValues(SparqlResponse response, List<String> expectedVars, int expectedRows) {

		Assert.assertTrue(!response.getVars().isEmpty());
		Assert.assertEquals(expectedVars, response.getVars());
		Assert.assertEquals(expectedRows, response.rows());
		Assert.assertEquals(expectedVars.size(), response.getResults().keySet().size());
	}
}