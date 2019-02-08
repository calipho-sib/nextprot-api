package org.nextprot.api.rdf.service.impl;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.rdf.service.SparqlEndpoint;
import org.nextprot.api.rdf.service.SparqlProxyEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class SparqlProxyEndpointImpl implements SparqlProxyEndpoint {

	@Autowired
	private SparqlEndpoint sparqlEndpoint;

	@Override
	@Cacheable(value = "sparql-proxy", sync = true)
	public ResponseEntity<String> sparql(String queryString) {
		return sparqlInternal(queryString);
	}

	@Override
	public ResponseEntity<String> sparqlNoCache(String queryString) {
		return sparqlInternal(queryString);
	}

	
	private ResponseEntity<String> sparqlInternal(String queryString) {

		ResponseEntity<String> responseEntity;
		String url = sparqlEndpoint.getUrl() + ((queryString != null) ? ("?" + queryString) : "");

		try {


			RestTemplate template = new RestTemplate();
			responseEntity = template.getForEntity(new URI(url), String.class);
			return responseEntity;

		} catch (HttpServerErrorException | HttpClientErrorException e) {
			throw new NextProtException(e.getResponseBodyAsString(), e);
		} catch (RestClientException | URISyntaxException e) {
			throw new NextProtException(e);
		} 
	}

}
