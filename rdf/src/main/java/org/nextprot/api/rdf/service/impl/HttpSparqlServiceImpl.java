package org.nextprot.api.rdf.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.rdf.service.HttpSparqlService;
import org.nextprot.api.rdf.service.SparqlEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class HttpSparqlServiceImpl implements HttpSparqlService {

	public static final String SPARQL_DEFAULT_URL = "https://sparql.nextprot.org";

	private static final String PREFIX = "PREFIX : <http://nextprot.org/rdf#>\n" +
			"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
			"PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +
			"PREFIX up: <http://purl.uniprot.org/core/>\n" +
			"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
			"PREFIX entry: <http://nextprot.org/rdf/entry/>\n" +
			"PREFIX isoform: <http://nextprot.org/rdf/isoform/>\n" +
			"PREFIX annotation: <http://nextprot.org/rdf/annotation/>\n" +
			"PREFIX evidence: <http://nextprot.org/rdf/evidence/>\n" +
			"PREFIX xref: <http://nextprot.org/rdf/xref/>\n" +
			"PREFIX publication: <http://nextprot.org/rdf/publication/>\n" +
			"PREFIX identifier: <http://nextprot.org/rdf/identifier/>\n" +
			"PREFIX cv: <http://nextprot.org/rdf/terminology/>\n" +
			"PREFIX gene: <http://nextprot.org/rdf/gene/>\n" +
			"PREFIX source: <http://nextprot.org/rdf/source/>\n" +
			"PREFIX db: <http://nextprot.org/rdf/db/>\n" +
			"PREFIX context: <http://nextprot.org/rdf/context/>\n" +
			"PREFIX interaction: <http://nextprot.org/rdf/interaction/>\n" +
			"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" +
			"PREFIX uniprot: <http://purl.uniprot.org/uniprot/>\n" +
			"PREFIX unipage: <http://www.uniprot.org/uniprot/>\n" +
			"PREFIX chebi: <http://purl.obolibrary.org/obo/>\n" +
			"PREFIX drugbankdrugs: <http://wifo5-04.informatik.uni-mannheim.de/drugbank/resource/drugs/>\n" +
			"PREFIX proteoform: <http://nextprot.org/rdf/proteoform/>\n\n";

	@Autowired
	private SparqlEndpoint sparqlEndpoint;

	@Override
	public SparqlResponse executeSparqlQuery(String query) {

		return executeSparqlQuery(sparqlEndpoint.getUrl(), query);
	}

	@Override
	public String executeSparqlQuery(String sparqlUrl, String query, String outputType) {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost post = new HttpPost(sparqlUrl);
		post.setHeader("Accept", "application/*");

		StringBuilder payload=new StringBuilder();
		try {
			List<NameValuePair> params = new ArrayList<>();

			params.add(new BasicNameValuePair("query", PREFIX + query));
			params.add(new BasicNameValuePair("output", outputType));
			post.setEntity(new UrlEncodedFormEntity(params));

			CloseableHttpResponse response = client.execute(post);

			try (BufferedReader in = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent()))) {

				String line;
				while ((line = in.readLine()) != null) {
					payload.append(line);
					payload.append(System.lineSeparator());
				}
			}
			return payload.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// http://uat-web2:8890/sparql
	// https://www.baeldung.com/httpclient-post-http-request
	@Override
	public SparqlResponse executeSparqlQuery(String sparqlUrl, String query) {

		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost post = new HttpPost(sparqlUrl);
		post.setHeader("Accept", "application/sparql-results+json");

		try {
			List<NameValuePair> params = new ArrayList<>();

			params.add(new BasicNameValuePair("query", PREFIX+query));
			post.setEntity(new UrlEncodedFormEntity(params));

			CloseableHttpResponse response = client.execute(post);
			Map<String, Object> rawContent = readRawJsonOutput(response);

			client.close();

			return sparqlResponse(rawContent);
		} catch (IOException e) {

			throw new NextProtException("could not search for sparql query (query="+query+")", e);
		}
	}

	private Map<String, Object> readRawJsonOutput(CloseableHttpResponse response) throws IOException {

		StringBuilder payload;

		try (BufferedReader in = new BufferedReader(
				new InputStreamReader(response.getEntity().getContent()))) {

			String line;
			payload = new StringBuilder();

			while ((line = in.readLine()) != null) {
				payload.append(line);
				payload.append(System.lineSeparator());
			}
		}

		//noinspection unchecked
		return new ObjectMapper().readValue(payload.toString(), Map.class);
	}

	private SparqlResponse sparqlResponse(Map<String, Object> rawJson) {

		SparqlResponse response = new SparqlResponse();

		if (!rawJson.containsKey("head")) {
			throw new NextProtException("missing 'head' key in json (json="+rawJson+")");
		}
		//noinspection unchecked
		Map<String, List<String>> head = (Map<String, List<String>>) rawJson.get("head");
		if (!rawJson.containsKey("results")) {
			throw new NextProtException("missing 'results' key in json (json="+rawJson+")");
		}
		//noinspection unchecked
		Map<String, Object> rawResults = (Map<String, Object>) rawJson.get("results");
		if (!rawResults.containsKey("bindings")) {
			throw new NextProtException("missing 'bindings' key in json (json="+rawJson+")");
		}
		//noinspection unchecked
		List<Map<String, Map<String, String>>> rawBindings =
				(List<Map<String, Map<String, String>>>) rawResults.get("bindings");

		for (Map<String, Map<String, String>> rawBinding : rawBindings) {

			for (String var : head.get("vars")) {

				response.addResult(var, rawBinding.get(var).get("value"));
			}
		}

		return response;
	}

}
