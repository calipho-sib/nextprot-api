package org.nextprot.api.rdf.service;

import org.springframework.http.ResponseEntity;

public interface SparqlProxyEndpoint {

	public ResponseEntity<String> sparql(String body, String queryString);
	public ResponseEntity<String> sparqlNoCache(String body, String queryString);

}
