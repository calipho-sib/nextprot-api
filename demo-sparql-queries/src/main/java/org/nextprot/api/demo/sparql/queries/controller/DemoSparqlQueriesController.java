package org.nextprot.api.demo.sparql.queries.controller;

import java.util.List;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.demo.sparql.queries.domain.DemoSparqlQuery;
import org.nextprot.api.demo.sparql.queries.service.DemoSparqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Api(name = "Demo Sparql Queries", description = "Demo sparql queries")
public class DemoSparqlQueriesController {

	@Autowired DemoSparqlService demoSparqlService;
	
	@ResponseBody
	@RequestMapping(value = "/demo/sparql/queries", method = { RequestMethod.GET }, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ApiMethod(path = "/demo/sparql/queries", verb = ApiVerb.GET, description = "Get demo sparql queries", produces={MediaType.APPLICATION_JSON_VALUE})
	public List<DemoSparqlQuery> getDemoSparalQueries() {
		demoSparqlService.relaodDemoSparqlQueries();
		return demoSparqlService.getDemoSparqlQueries();
	}
	

	@ResponseBody
	@RequestMapping(value = "/demo/sparql/queries/reload", method = { RequestMethod.GET })
	@ApiMethod(path = "/demo/sparql/queries/reload", verb = ApiVerb.GET, description = "Reload demo sparql queries")
	public String reloadDemoSparqlQueries() {
		demoSparqlService.relaodDemoSparqlQueries();
		return "Reload complete";
	}
}
