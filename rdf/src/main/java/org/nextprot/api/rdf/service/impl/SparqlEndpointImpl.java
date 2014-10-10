package org.nextprot.api.rdf.service.impl;

import org.nextprot.api.rdf.service.SparqlEndpoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SparqlEndpointImpl implements SparqlEndpoint {

	private String url;
	private String timeout;

	public String getUrl() {
		return url;
	}

	@Value("${sparql.url}")
	public void setUrl(String url) {
		this.url = url;
	}

	public String getTimeout() {
		return timeout;
	}

	@Value("${sparql.timeout}")
	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}


}
