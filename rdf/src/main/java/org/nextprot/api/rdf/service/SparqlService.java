package org.nextprot.api.rdf.service;

import java.util.List;

import org.nextprot.api.commons.utils.SparqlResult;
import org.springframework.beans.factory.annotation.Value;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.sparql.resultset.ResultsFormat;

public interface SparqlService {

	/**
	 * Execute a SPARQL search query
	 * 
	 * @param query
	 */
	List<String> findEntries(String sparql, String sparqlEndpointUrl, String queryTitle);

	/**
	 * Executes a SPARQL query without any cache
	 * 
	 * @param queryString
	 * @param sparqlEndpoint
	 * @param queryTitle
	 * @return
	 */
	List<String> findEntriesNoCache(@Value("sparql") String queryString, @Value("sparqlEndpoint") String sparqlEndpoint, @Value("sparqlTitle") String queryTitle, @Value("testId") String testId);

	/**
	 * Proxy
	 * 
	 * @param sparql
	 * @param sparqlEndpointUrl
	 * @param queryTitle
	 * @return
	 */
	public SparqlResult sparqlSelect(@Value("sparql") String sparql, @Value("sparqlEndpoint") String sparqlEndpointUrl, @Value("timeout") int timeout, @Value("sparqlTitle") String queryTitle, @Value("testId") String testId,  ResultsFormat format);

	QueryExecution queryExecution(String query);

}
