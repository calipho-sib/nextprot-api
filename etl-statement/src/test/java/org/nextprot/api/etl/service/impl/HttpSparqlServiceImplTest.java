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

	@Test
	public void testSearch() {

		HttpSparqlService sparqlService = new HttpSparqlServiceImpl();

		SparqlResponse response = sparqlService.executeSparqlQuery(SPARQL_DEFAULT_URL,
				"select distinct ?entry ?glypos where {\n" +
						"values (?entry ?glypos) {\n" +
						" (entry:NX_O75396 \"172\"^^xsd:integer)\n" +
						" (entry:NX_O95049 \"86\"^^xsd:integer)\n" +
						" (entry:NX_Q76I76 \"339\"^^xsd:integer)\n" +
						" }\n" +
						"\n" +
						"?entry :isoform ?iso.\n" +
						"?iso :swissprotDisplayed true .\n" +
						"\n" +
						"{\n" +
						"values ?forbidtopodom {\n" +
						"cv:CVTO_0001\n" +
						"cv:CVTO_0004\n" +
						"cv:CVTO_0013\n" +
						"cv:CVTO_0015\n" +
						"cv:CVTO_0022\n" +
						"} # topo check\n" +
						"  ?iso :topology ?topodom .\n" +
						"  ?topodom :term ?forbidtopodom; :start ?topodomstart; :end ?topodomend .\n" +
						"  filter((?glypos >= ?topodomstart) && (?glypos <= ?topodomend))\n" +
						"}\n" +
						"  union\n" +
						"{\n" +
						"values ?forbiddom {\n" +
						"cv:DO-00843\n" +
						"cv:DO-00082\n" +
						"cv:DO-00096\n" +
						"cv:DO-00098\n" +
						"cv:DO-00099\n" +
						"cv:DO-00100\n" +
						"cv:DO-00127\n" +
						"cv:DO-00135\n" +
						"cv:DO-00212\n" +
						"cv:DO-00218\n" +
						"cv:DO-00224\n" +
						"cv:DO-00234\n" +
						"cv:DO-00847\n" +
						"cv:DO-00280\n" +
						"cv:DO-00282\n" +
						"cv:DO-00302\n" +
						"cv:DO-00310\n" +
						"cv:DO-00341\n" +
						"cv:DO-00343\n" +
						"cv:DO-00349\n" +
						"cv:DO-00350\n" +
						"cv:DO-00354\n" +
						"cv:DO-00376\n" +
						"cv:DO-00378\n" +
						"cv:DO-00404\n" +
						"cv:DO-00416\n" +
						"cv:DO-00418\n" +
						"cv:DO-00421\n" +
						"cv:DO-00415\n" +
						"cv:DO-00430\n" +
						"cv:DO-00462\n" +
						"cv:DO-00466\n" +
						"cv:DO-00467\n" +
						"cv:DO-00469\n" +
						"cv:DO-00477\n" +
						"cv:DO-00869\n" +
						"cv:DO-00555\n" +
						"cv:DO-00592\n" +
						"cv:DO-00602\n" +
						"cv:DO-00604\n" +
						"cv:DO-00779\n" +
						"cv:DO-00918\n" +
						"cv:DO-00943\n" +
						"cv:DO-00632\n" +
						"cv:DO-00636\n" +
						"cv:DO-00671\n" +
						"cv:DO-00691\n" +
						"cv:DO-00695\n" +
						"cv:DO-00700\n" +
						"cv:DO-00832\n" +
						"cv:DO-00741\n" +
						"cv:DO-00078\n" +
						"cv:DO-00057\n" +
						"cv:DO-00104\n" +
						"cv:DO-00144\n" +
						"cv:DO-00244\n" +
						"cv:DO-00273\n" +
						"cv:DO-00284\n" +
						"cv:DO-00387\n" +
						"cv:DO-00451\n" +
						"cv:DO-00561\n" +
						"cv:DO-00650\n" +
						"cv:DO-00658\n" +
						"cv:DO-00692\n" +
						"cv:DO-00697\n" +
						"cv:DO-00707\n" +
						"}  # domain check\n" +
						"  ?iso :domain ?dom .\n" +
						"  ?dom :term ?forbiddom; :start ?domstart; :end ?domend .\n" +
						"  filter((?glypos >= ?domstart) && (?glypos <= ?domend))\n" +
						"  }\n" +
						"  union\n" +
						" {\n" +
						"  ?iso :signalPeptide ?sigpep .\n" +
						"  ?sigpep :start ?sigtart; :end ?sigend .\n" +
						"  filter((?glypos >= ?sigtart) && (?glypos <= ?sigend))\n" +
						" }\n" +
						"  union\n" +
						" {\n" +
						"  ?iso :mitochondrialTransitPeptide ?trpep .\n" +
						"  ?trpep :start ?trtart; :end ?trend .\n" +
						"  filter((?glypos >= ?trtart) && (?glypos <= ?trend))\n" +
						" }\n" +
						"}  order by ?entry ?glypos");

		checkExpectedValues(response, Arrays.asList("entry", "glypos"), 3);

		Assert.assertEquals(Arrays.asList("http://nextprot.org/rdf/entry/NX_O75396", "http://nextprot.org/rdf/entry/NX_O95049",
				"http://nextprot.org/rdf/entry/NX_Q76I76"), response.getResults("entry"));
		Assert.assertEquals(Arrays.asList("172", "86", "339"), response.getResults("glypos"));
		Assert.assertEquals(Arrays.asList(172, 86, 339), response.castResults("glypos", Integer.class));
	}

	private void checkExpectedValues(SparqlResponse response, List<String> expectedVars, int expectedRows) {

		Assert.assertTrue(!response.getVars().isEmpty());
		Assert.assertEquals(expectedVars, response.getVars());
		Assert.assertEquals(expectedRows, response.rows());
	}
}