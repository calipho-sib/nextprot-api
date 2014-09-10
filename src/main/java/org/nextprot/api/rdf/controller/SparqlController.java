package org.nextprot.api.rdf.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.rdf.service.SparqlEndpoint;
import org.nextprot.api.rdf.service.SparqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hp.hpl.jena.sparql.resultset.ResultsFormat;

/**
 * Controller used many to log and tune queries. Check the log times. No cache is used on purpose
 * 
 * @author dteixeira
 */
@Lazy
@Controller
@PreAuthorize("hasRole('ROLE_SPARQL')")
@Api(name = "Sparql", description = "Sparql endpoint where SPARQL queries are available", role="ROLE_SPARQL")
public class SparqlController {

	@Autowired
	private SparqlService sparqlService;
	@Autowired
	private SparqlEndpoint sparqlEndpoint;
	
	@RequestMapping(value = "/sparql-nocache", method = { RequestMethod.GET })
	@ResponseBody
	public List<String> sparqlNoCache(@RequestParam(value = "sparql", required = true) String queryString, 
			@RequestParam(value = "sparqlTitle", required = true) String queryTitle,
			@RequestParam(value = "sparqlEndpoint", required = true) String sparqlEndpoint, 
			@RequestParam(value = "testId", required = false) String testId, Model model) {

		return sparqlService.findEntriesNoCache(queryString, sparqlEndpoint, queryTitle, testId);
	}

	@RequestMapping(value = "/sparql")
	@ResponseBody
	@ApiMethod(path = "/sparql", verb = ApiVerb.GET, description = "Sparql endpoint", produces = { MediaType.APPLICATION_XML_VALUE , MediaType.APPLICATION_JSON_VALUE, "text/turtle"})
	public String sparql(HttpServletRequest request, HttpServletResponse response,
			
			@ApiParam(name = "query", description = "The SPARQL query",  allowedvalues = { "SELECT DISTINCT * WHERE {?s ?p ?o} LIMIT 10"})
			@RequestParam(value = "query", required = false) String query, 

			@RequestParam(value = "output", required = false) String output,
			
			@RequestParam(value = "testid", required = false) String testid,
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "engine", required = false) String engine) {

		String format = output;
		if(format == null){
			format = request.getHeader("Accept");
		}
		
		if(engine == null){
			engine = sparqlEndpoint.getUrl();
		}
		
		return sparqlService.sparqlSelect(query, engine, Integer.parseInt(sparqlEndpoint.getTimeout()), title, testid,  ResultsFormat.guessSyntax(format, ResultsFormat.FMT_RS_XML)).getOutput();
	}

}
