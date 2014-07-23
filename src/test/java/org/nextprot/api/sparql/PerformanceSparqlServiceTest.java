package org.nextprot.api.sparql;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nextprot.api.core.dao.RepositoryUserQueryDao;
import org.nextprot.api.core.domain.UserQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("pro")
@ContextConfiguration("classpath:META-INF/spring/core-context.xml")
public class PerformanceSparqlServiceTest {

	private static final String BASE_URL = "http://crick:8080/nextprot-api/sparql-nocache?";

	private enum SparqlEndpoints {

		KANT_FUSEKI("http://kant:3030/np/query?output=json"), KANT_VIRTUOSO("http://kant:8890/sparql"), UATWEB2_FUSEKI("http://uat-web2:3030/np/query?output=json"), UATWEB2_VIRTUOSO(
				"http://uat-web2:8890/sparql");

		private String url;

		SparqlEndpoints(String url) {
			this.url = url;
		}

		public String getUrl() {
			return url;
		}

	}

	@Autowired
	private RepositoryUserQueryDao repositoryUserDao;

	private static String generateTestId() {
		String newstring = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
		String testId = "TEST-SPARQL-QUERIES-" + newstring;
		return testId;
	}

	@Test
	public void fullNextProtEntries() {
		List<UserQuery> userQueries = repositoryUserDao.getNextprotQueries();
		String testId = generateTestId();
		for (SparqlEndpoints sparqlEP : SparqlEndpoints.values()) {
			for (UserQuery uq : userQueries) {

				System.out.println("Sending " + uq.getTitle());
				String sparql = uq.getSparql();

				// String sparql = "?entry :isoform/:expression/:in/:childOf term:TS-0564;\n\t:classifiedWith term:KW-0813.";
				try {
					sendRequest(sparql, sparqlEP, "\"" + uq.getTitle() + "\"", testId);
				} catch (Exception e) {
					e.printStackTrace();
					break; // Goes to next sparql end point
				}
			}

		}
		System.out.println("http://crick:8000/en-US/app/search/sparql_queries_time_performance??earliest=0&latest=&form.testId=" + testId);
	}

	private static void sendRequest(String sparql, SparqlEndpoints sparqlEndpoint, String sparqlTitle, String testId) throws Exception {

		String url = BASE_URL;

		url += ("sparql=" + URLEncoder.encode(sparql, "UTF-8"));
		url += ("&sparqlTitle=" + URLEncoder.encode(sparqlTitle, "UTF-8"));
		url += ("&sparqlEndpoint=" + sparqlEndpoint.getUrl());
		url += ("&testId=" + testId);

		System.err.println("Sending " + url);

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		// add request header
		con.setRequestProperty("User-Agent", "Mozilla/5.0");

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

	}
}
