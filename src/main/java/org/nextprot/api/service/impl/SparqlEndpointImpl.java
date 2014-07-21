package org.nextprot.api.service.impl;

import org.nextprot.api.service.SparqlEndpoint;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

public class SparqlEndpointImpl implements SparqlEndpoint {

	private String url;
	private String timeout;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	@Override
	public QueryExecution queryExecution(String query) {

		QueryEngineHTTP qExec = (QueryEngineHTTP) QueryExecutionFactory.sparqlService(url, query);
		qExec.addParam("timeout", timeout);
		return qExec;
	}


}
