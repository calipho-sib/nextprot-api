package org.nextprot.api.demo.sparql.queries.service;

import java.util.List;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.demo.example.queries.DemoSparqlQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Api(name = "Demo Sparql Queries", description = "Demo sparql queries")
public class DemoSparqlQueriesController {

	@Autowired DemoSparqlService demoSparqlService;
	
	@ResponseBody
	@RequestMapping(value = "/demo/sparql/queries", method = { RequestMethod.GET })
	@ApiMethod(path = "/demo/sparql/queries", verb = ApiVerb.GET, description = "Get demo sparql queries")
	public List<DemoSparqlQuery> getDemoSparalQueries() {
		return demoSparqlService.getDemoSparqlQueries();
	}
	
}
