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

@Ignore
public class RunAllSparqlQueriesApp {

	//This will log on release-info folder in a file called sparql-queries.tsv
	private static final Log LOGGER = LogFactory.getLog(RunAllSparqlQueriesApp.class);

	private static final String SPARQL_ENDPOINT = "http://kant:8890/sparql";
	//private static final String SPARQL_ENDPOINT = "http://godel:8890/sparql";
	private static final String QUERIES_URL = "http://alpha-api.nextprot.org/queries/tutorial.json?snorql=true";
	//private static final String QUERIES_URL = "https://api.nextprot.org/queries/tutorial.json?snorql=true";

	public static void main(String[] args) throws Exception {
/*
		String prefixes = "PREFIX :<http://nextprot.org/rdf#>\n" + "PREFIX annotation:<http://nextprot.org/rdf/annotation/>\n" + "PREFIX context:<http://nextprot.org/rdf/context/>\n"
				+ "PREFIX cv:<http://nextprot.org/rdf/terminology/>\n" + "PREFIX db:<http://nextprot.org/rdf/db/>\n" + "PREFIX dc:<http://purl.org/dc/elements/1.1/>\n"
				+ "PREFIX dcterms:<http://purl.org/dc/terms/>\n" + "PREFIX entry:<http://nextprot.org/rdf/entry/>\n" + "PREFIX evidence:<http://nextprot.org/rdf/evidence/>\n"
				+ "PREFIX foaf:<http://xmlns.com/foaf/0.1/>\n" + "PREFIX gene:<http://nextprot.org/rdf/gene/>\n" + "PREFIX identifier:<http://nextprot.org/rdf/identifier/>\n"
				+ "PREFIX isoform:<http://nextprot.org/rdf/isoform/>\n" + "PREFIX mo:<http://purl.org/ontology/mo/>\n" + "PREFIX ov:<http://open.vocab.org/terms/>\n"
				+ "PREFIX owl:<http://www.w3.org/2002/07/owl#>\n" + "PREFIX publication:<http://nextprot.org/rdf/publication/>\n" + "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>\n" + "PREFIX sim:<http://purl.org/ontology/similarity/>\n" + "PREFIX source:<http://nextprot.org/rdf/source/>\n"
				+ "PREFIX xref:<http://nextprot.org/rdf/xref/>\n" + "PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>";$
				*/
		
		String prefixes = "PREFIX :<http://nextprot.org/rdf#>\n" + 
				"PREFIX annotation:<http://nextprot.org/rdf/annotation/>\n" + 
				"PREFIX context:<http://nextprot.org/rdf/context/>\n" + 
				"PREFIX cv:<http://nextprot.org/rdf/terminology/>\n" + 
				"PREFIX db:<http://nextprot.org/rdf/db/>\n" + 
				"PREFIX dc:<http://purl.org/dc/elements/1.1/>\n" + 
				"PREFIX dcterms:<http://purl.org/dc/terms/>\n" + 
				"PREFIX entry:<http://nextprot.org/rdf/entry/>\n" + 
				"PREFIX evidence:<http://nextprot.org/rdf/evidence/>\n" + 
				"PREFIX foaf:<http://xmlns.com/foaf/0.1/>\n" + 
				"PREFIX gene:<http://nextprot.org/rdf/gene/>\n" + 
				"PREFIX identifier:<http://nextprot.org/rdf/identifier/>\n" + 
				"PREFIX isoform:<http://nextprot.org/rdf/isoform/>\n" + 
				"PREFIX mo:<http://purl.org/ontology/mo/>\n" + 
				"PREFIX ov:<http://open.vocab.org/terms/>\n" + 
				"PREFIX owl:<http://www.w3.org/2002/07/owl#>\n" + 
				"PREFIX publication:<http://nextprot.org/rdf/publication/>\n" + 
				"PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + 
				"PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>\n" + 
				"PREFIX sim:<http://purl.org/ontology/similarity/>\n" + 
				"PREFIX source:<http://nextprot.org/rdf/source/>\n" + 
				"PREFIX term:<http://nextprot.org/rdf/terminology/>\n" + 
				"PREFIX xref:<http://nextprot.org/rdf/xref/>\n" + 
				"PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>";

		boolean testFailed = false;

		List<UserQuery> queries = getSparqlQueries();
		System.out.println("Found " + queries.size() + " queries") ;
		
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

			} catch (Exception e) {
				e.printStackTrace();
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

		assertFalse(testFailed);

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
			qExec.setTimeout(2 * 60 * 1000); //2 min
			
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
