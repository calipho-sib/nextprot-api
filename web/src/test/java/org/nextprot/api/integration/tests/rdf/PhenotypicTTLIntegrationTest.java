package org.nextprot.api.integration.tests.rdf;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.InputStream;

import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.rdf.utils.SparqlDictionary;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.springframework.beans.factory.annotation.Autowired;

public class PhenotypicTTLIntegrationTest extends WebIntegrationBaseTest {

	@Autowired
	private SparqlDictionary sparqlDictionary;
	

	private Repository testRepo = null;

	@Before
	public void init() throws Exception {
		
		String prefixes = this.mockMvc.perform(get("/common-prefixes.ttl")).andReturn().getResponse().getContentAsString();

		// Prepare repository
		String ttlContent = this.mockMvc.perform(get("/entry/NX_Q15858.ttl")).andReturn().getResponse().getContentAsString();
		// Daniel: Missing terminologies and other things I guess...

		String ttl = prefixes + ttlContent;
		//FileWriter w = new FileWriter("toto");
		//w.write(ttl);
		//w.close();
		//System.out.println(ttl);
		
		
		testRepo = new SailRepository(new MemoryStore());
		testRepo.initialize();

		InputStream stream = new ByteArrayInputStream((ttl).getBytes());
		testRepo.getConnection().add(stream, "", RDFFormat.TURTLE, new Resource[] {});

	}

	private TupleQueryResult doTupleQuery(String sparqlQuery) throws RepositoryException, QueryEvaluationException, MalformedQueryException {

		RepositoryConnection conn = testRepo.getConnection();
		TupleQuery query = (TupleQuery) conn.prepareQuery(QueryLanguage.SPARQL, sparqlQuery);
		TupleQueryResult result = query.evaluate();

		return result;
	}

	@Test
	public void shouldReturnCorrectTTLAndBeAbleToPerformAnRDFQuery() throws Exception {


		// String sparqlQuery = sparqlDictionary.getSparqlOnly("NXQ_00147");
		// //For phenotypic
		String sparqlQuery = "SELECT (COUNT(*) AS ?no) where { ?s ?p ?o  }";
		TupleQueryResult result = doTupleQuery(sparqlQuery);
		//System.err.println(result.getBindingNames().iterator().next());
		long numberOfTriples = Long.valueOf(result.next().getBinding("no").getValue().stringValue());
		Assert.assertTrue(numberOfTriples > 1000);
		result.close();

	}

}