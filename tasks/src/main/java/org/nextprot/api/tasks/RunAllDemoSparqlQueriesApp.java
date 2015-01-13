package org.nextprot.api.tasks;

import static org.junit.Assert.assertFalse;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.utils.SparqlUtils;
import org.nextprot.api.demo.sparql.queries.service.DemoSparqlService;
import org.nextprot.api.rdf.utils.SparqlDictionary;
import org.nextprot.api.user.domain.UserQuery;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;

public class RunAllDemoSparqlQueriesApp {

	private static final Log LOGGER = LogFactory.getLog(RunAllDemoSparqlQueriesApp.class);

	// String sparqlEndpoint = "http://crick:8080/nextprot-api-web/sparql"; //TODO fix this
	private static final String SPARQL_ENDPOINT = "http://kant:8890/sparql";

	
	public static void main(String[] args) {

		System.setProperty("spring.profiles.active", "dev");
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:spring/commons-context.xml", "spring/demo-queries-context.xml");

		DemoSparqlService demoSparqlService = ctx.getBean(DemoSparqlService.class);
		SparqlDictionary sparqlDictionary = ctx.getBean(SparqlDictionary.class);

		
		boolean testFailed = false;

		List<UserQuery> queries = demoSparqlService.getDemoSparqlQueries();
		for (UserQuery q : queries) {

			long start = System.currentTimeMillis();
			String errorMessage = "";
			int resultsCount = 0;

			try {

				String sparqlQuery = SparqlUtils.buildQuery(sparqlDictionary.getSparqlPrefixes(), q.getSparql());
				resultsCount = executeQuery(sparqlQuery);

			} catch (Exception e) {
				errorMessage = e.getLocalizedMessage();
			} finally {

				if (resultsCount == 0) {
					testFailed = true;
				}

				long timeSpent = ((System.currentTimeMillis() - start) / 1000);
				LOGGER.info(q.getTitle() + "\t" + timeSpent + "\t" + resultsCount + "\t" + errorMessage);
			}

		}

		assertFalse(testFailed);

	}
	
	
	
	private static int executeQuery(String query) {

		int resultsCount = 0;
		QueryExecution qExec = null;
		try {
			qExec = QueryExecutionFactory.sparqlService(SPARQL_ENDPOINT, query);
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
