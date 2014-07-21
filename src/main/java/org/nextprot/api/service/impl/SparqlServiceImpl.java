package org.nextprot.api.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.nextprot.api.domain.exception.NextProtException;
import org.nextprot.api.service.SparqlService;
import org.nextprot.utils.FileUtils;
import org.nextprot.utils.SparqlResult;
import org.nextprot.utils.SparqlUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.resultset.ResultsFormat;

@Service
public class SparqlServiceImpl implements SparqlService, InitializingBean {

	private static final String ENTRY_SUFFIX_URI = "http://nextprot.org/rdf/entry/";
	private String prefix = null;

	@Override
	@Cacheable("sparql")
	public List<String> findEntries(String sparql, String sparqlEndpointUrl, String sparqlTitle) {

		String query = buildQuery(sparql);

		List<String> results = new ArrayList<String>();
		QueryExecution qExec = null;

		try {
			qExec = QueryExecutionFactory.sparqlService(sparqlEndpointUrl, query);
		} catch (QueryParseException qe) {
			throw new NextProtException("Malformed SPARQL: " + qe.getLocalizedMessage());
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
				throw new NextProtException("Bind your protein result to a variable called ?entry. Example: \"?entry :classifiedWith term:KW-0813.\"");
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
		prefix = FileUtils.readResourceAsString("/sparql/prefix.rq");
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
			throw new NextProtException("Malformed SPARQL: " + qe.getLocalizedMessage());
		}

		return result;

	}

}
