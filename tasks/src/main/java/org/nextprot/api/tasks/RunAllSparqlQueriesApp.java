package org.nextprot.api.tasks;

import static org.junit.Assert.assertFalse;

import java.net.URL;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Ignore;
import org.nextprot.api.user.domain.UserQuery;
import org.nextprot.api.user.utils.UserQueryUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;

//@Ignore
public class RunAllSparqlQueriesApp {

	//This will log on release-info folder in a file called sparql-queries.tsv
	private static final Log LOGGER = LogFactory.getLog(RunAllSparqlQueriesApp.class);

	private static final String SPARQL_ENDPOINT = "http://uat-web2:8890/sparql";
	//private static final String SPARQL_ENDPOINT = "http://godel:8890/sparql";
	private static final String QUERIES_URL = "http://alpha-api.nextprot.org/queries/tutorial.json?snorql=true";
	//private static final String QUERIES_URL = "https://api.nextprot.org/queries/tutorial.json?snorql=true";
	
	private static final String PREFIXES_URL = "http://alpha-api.nextprot.org/sparql-prefixes";
	//private static final String PREFIXES_URL = "http://localhost:8080/nextprot-api-web/sparql-prefixes";


	public static void main(String[] args) throws Exception {
		
		String prefixes = getSparqlPrefixes();

		boolean testFailed = false;

		List<UserQuery> queries = getSparqlQueries();
		System.out.println("Found " + queries.size() + " queries") ;
		
		int exceptionCount=0;
		int zeroResultsCount=0;
		for (UserQuery q : queries) {

			// if (q.getUserQueryId()!=124) continue;

			long start = System.currentTimeMillis();
			String errorMessage = "";
			int resultsCount = 0;

			try {

				String sparqlQuery = prefixes + "\n" + q.getSparql();
				System.out.println("\nRequesting " + q.getPublicId() + " (" + q.getDescription() + ")... \n\n" + sparqlQuery + "\n\n");
				// System.out.println("\n\nsparqlQuery=\n"+sparqlQuery +1
				// "\n---------------");
				resultsCount = executeQuery(sparqlQuery);
				if (resultsCount==0) zeroResultsCount++;

			} catch (Exception e) {
				e.printStackTrace();
				exceptionCount++;
				errorMessage = e.getLocalizedMessage();
			} finally {

				if (resultsCount == 0) {
					testFailed = true;
					
				}

				long timeSpent = ((System.currentTimeMillis() - start) / 1000);
				String qn = UserQueryUtils.getTutoQueryNameFromId(q.getUserQueryId());
				
				//This will log on release-info folder in a file called sparql-queries.tsv
				LOGGER.info(qn + "\t" + timeSpent + "\t" + resultsCount + "\t" + q.getTitle() + "\t" + errorMessage);
			}

		}

		LOGGER.info("Query count:" + queries.size() );
		LOGGER.info("OK count:" + (queries.size() - exceptionCount - zeroResultsCount) );
		LOGGER.info("Exception count:" + exceptionCount );
		LOGGER.info("ZeroResult count:" + zeroResultsCount );
		assertFalse(testFailed);

	}

	

	private static String getSparqlPrefixes() throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		URL queriesUrl = new URL(PREFIXES_URL);
		List<String> prefixes = mapper.readValue(queriesUrl, new TypeReference<List<String>>() {});
		StringBuilder sb = new StringBuilder();
		for(String pref : prefixes)
			sb.append(pref).append("\n");
		return sb.toString();
	}

	
	private static List<UserQuery> getSparqlQueries() throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		URL queriesUrl = new URL(QUERIES_URL);
		return mapper.readValue(queriesUrl, new TypeReference<List<UserQuery>>() {});

	}

	private static int executeQuery(String query) {

		int resultsCount = 0;
		QueryExecution qExec = null;
		try {
			qExec = QueryExecutionFactory.sparqlService(SPARQL_ENDPOINT, query);
			qExec.setTimeout(10 * 60 * 1000); //10 min
			
			ResultSet rs = qExec.execSelect();
			while (rs.hasNext()) {
				rs.nextBinding();
				resultsCount++;
			}

		} finally {
			if (qExec != null) {
				qExec.close();
			}
		}

		return resultsCount;

	}

}
