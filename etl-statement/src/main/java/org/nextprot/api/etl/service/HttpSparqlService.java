package org.nextprot.api.etl.service;

import java.util.Map;

public interface HttpSparqlService {

	/**
	 * @param sparqlUrl the sparql url
	 * @param query the sparql query
	 * @return response data
	 */
	Map<String, Object> executeSparqlQuery(String sparqlUrl, String query);

	Map<String, Object> executeSparqlQuery(String query);
}
