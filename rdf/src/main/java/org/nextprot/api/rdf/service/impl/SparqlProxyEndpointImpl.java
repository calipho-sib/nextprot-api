package org.nextprot.api.rdf.service.impl;

import java.net.URI;
import java.net.URISyntaxException;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.rdf.service.SparqlEndpoint;
import org.nextprot.api.rdf.service.SparqlProxyEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class SparqlProxyEndpointImpl implements SparqlProxyEndpoint {

	@Autowired
	private SparqlEndpoint sparqlEndpoint;

	@Override
	@Cacheable("sparql-proxy")
	public ResponseEntity<String> sparql(String body, String queryString) {
		return sparqlInternal(body, queryString);
	}

	
	@Override
	public ResponseEntity<String> sparqlNoCache(String body, String queryString) {
		return sparqlInternal(body, queryString);
	}

	
	private ResponseEntity<String> sparqlInternal(String body, String queryString) {

		ResponseEntity<String> responseEntity;
		String url = sparqlEndpoint.getUrl() + ((queryString != null) ? ("?" + queryString) : "");

		RestTemplate template = new RestTemplate();
		try {

			responseEntity = template.exchange(new URI(url), HttpMethod.GET, new HttpEntity<String>(body), String.class);
			return responseEntity;

		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw new NextProtException(e.getResponseBodyAsString(), e);
		} catch (RestClientException | URISyntaxException e) {
			throw new NextProtException(e);
		} 
	}

}
