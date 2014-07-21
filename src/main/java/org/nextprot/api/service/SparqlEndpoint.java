package org.nextprot.api.service;

import com.hp.hpl.jena.query.QueryExecution;


public interface SparqlEndpoint {

	public String getTimeout();

	public String getUrl();

	public QueryExecution queryExecution(String query);

}
