package org.nextprot.api.rdf.service.impl;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;
import com.hp.hpl.jena.sparql.resultset.ResultsFormat;
import org.nextprot.api.commons.exception.ExceptionUtils;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.SparqlResult;
import org.nextprot.api.commons.utils.SparqlUtils;
import org.nextprot.api.rdf.service.SparqlEndpoint;
import org.nextprot.api.rdf.service.SparqlService;
import org.nextprot.api.rdf.utils.SparqlDictionary;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SparqlServiceImpl implements SparqlService, InitializingBean {

	private static final String ENTRY_SUFFIX_URI = "http://nextprot.org/rdf/entry/";
	private String prefix = null;
	
	@Autowired private SparqlDictionary sparqlDictionary = null;
	
	@Autowired private SparqlEndpoint sparqlEndpoint = null;


	@Override
	@Cacheable(value = "sparql", sync = true)
	public List<String> findEntries(String sparql, String sparqlEndpointUrl, String sparqlTitle) {

		String query = SparqlUtils.buildQuery(prefix, sparql);

		List<String> results = new ArrayList<String>();
		QueryExecution qExec = null;

		try {
			qExec = QueryExecutionFactory.sparqlService(sparqlEndpointUrl, query);
		} catch (QueryParseException qe) {
			String msg = ExceptionUtils.fixLineNumberInErrorMessage(qe.getLocalizedMessage());
			throw new NextProtException("Malformed SPARQL: " + msg);
		}

		ResultSet rs = qExec.execSelect();

		/**
		 * This give an empty graph....
		 * Model m = rs.getResourceModel();
		 * Graph g = m.getGraph();
		 * System.err.println("The graph is" + g);
		 */

		Var x = Var.alloc("entry");
		while (rs.hasNext()) {
			Binding b = rs.nextBinding();
			Node entryNode = b.get(x);
			if (entryNode == null) {
				qExec.close();
				throw new NextProtException("Bind your protein result to a variable called ?entry. Example: \"?entry :classifiedWith cv:KW-0813.\"");
			} else if (entryNode.toString().indexOf(ENTRY_SUFFIX_URI) == -1) {
				qExec.close();
				throw new NextProtException("Any entry found in the output, however was found: " + entryNode.toString());
			}

			String entry = entryNode.toString().replace(ENTRY_SUFFIX_URI, "").trim();
			results.add(entry);
		}
		qExec.close();
		return results;

	}

	private String buildQuery(String query) {

		// If it does not start with prefix
		if (!query.trim().toUpperCase().startsWith("PREFIX")) {
			String resultQuery = "";
			resultQuery += prefix;

			// and if does not start with select
			boolean selectIncluded = false;
			if (!query.trim().toUpperCase().startsWith("SELECT")) {
				resultQuery += "SELECT distinct ?entry {\n";
				selectIncluded = true;
			}

			resultQuery += query;
			if (selectIncluded)
				resultQuery += "\n}";

			return resultQuery;

		} else {

			String resultQuery = "";
			resultQuery += prefix;
			resultQuery += query;
			return resultQuery;

		}

	}

	// private static String removeComments (String query){
	// TODO make a better regex not to include things like
	// PREFIX : <http://nextprot.org/rdf#> --> PREFIX : <http://nextprot.org/rdf
	// return query;

	// return query.replaceAll("#.*(?=\\n)", "");
	// }

	@Override
	public void afterPropertiesSet() throws Exception {
		prefix = sparqlDictionary.getSparqlPrefixes();
	}

	@Override
	public List<String> findEntriesNoCache(String queryString, String sparqlEndpoint, String queryTitle, String titleId) {
		// Should not take the cache because does not go through the proxy
		return this.findEntries(queryString, sparqlEndpoint, queryTitle);
	}

	@Override
	public SparqlResult sparqlSelect(String sparql, String sparqlEndpointUrl, int timeout, String queryTitle, String testId, ResultsFormat format) {

		SparqlResult result = null;
		try {
			
			QueryExecution qExec = QueryExecutionFactory.sparqlService(sparqlEndpointUrl, sparql);
			qExec.setTimeout(timeout);
			ResultSet rs = qExec.execSelect();
			result = SparqlUtils.convertResultToFormat(rs, format);
			qExec.close();

		} catch (QueryParseException qe) {
			String msg = ExceptionUtils.fixLineNumberInErrorMessage(qe.getLocalizedMessage());
			throw new NextProtException("Malformed SPARQL: " + msg);
		}

		return result;

	}

	@Override
	public QueryExecution queryExecution(String query) {
		QueryEngineHTTP qExec = (QueryEngineHTTP) QueryExecutionFactory.sparqlService(sparqlEndpoint.getUrl(), query);
		//qExec.addParam("timeout", sparqlEndpoint.getTimeout());
		return qExec;
	}
	
	
}
