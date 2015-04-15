package org.nextprot.api.rdf.controller;

import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.rdf.service.SparqlProxyEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller used many to log and tune queries. Check the log times. No cache is used on purpose
 * 
 * @author dteixeira
 */
@Lazy
@Controller
public class SparqlController {

	@Autowired
	private SparqlProxyEndpoint sparqlProxyEndpoint;
	
	@RequestMapping("/sparql")
	@ResponseBody
	public ResponseEntity<String> mirrorRest(@RequestBody String body, HttpServletRequest request, HttpServletResponse response) throws URISyntaxException {
		String queryString = request.getQueryString();
		if(request.getParameter("query") == null){
			throw new NextProtException("Please provide a SPARQL query");
		}
		return this.sparqlProxyEndpoint.sparql(body, queryString);
	}


	/*@Autowired
	private SparqlService sparqlService;

	@Autowired
	private SparqlEndpoint sparqlEndpoint;
	*/
	/*
	@RequestMapping(value = "/sparql-nocache", method = { RequestMethod.GET })
	@ResponseBody
	public List<String> sparqlNoCache(@RequestParam(value = "sparql", required = true) String queryString, 
			@RequestParam(value = "sparqlTitle", required = true) String queryTitle,
			@RequestParam(value = "sparqlEndpoint", required = true) String sparqlEndpoint, 
			@RequestParam(value = "testId", required = false) String testId, Model model) {

		return sparqlService.findEntriesNoCache(queryString, sparqlEndpoint, queryTitle, testId);
	}

	@RequestMapping(value = "/sparqlite")
	@ResponseBody
	public String sparql(HttpServletRequest request, HttpServletResponse response,
			
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
	*/
	

}
