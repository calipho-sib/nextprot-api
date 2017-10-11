package org.nextprot.api.web.controller;

import org.codehaus.jackson.map.ObjectMapper;
import org.jsondoc.core.annotation.Api;
import org.nextprot.api.web.service.GraphQlExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@Api(name = "GraphQlController", description = "Method to retrieve data using graphql syntax")
public class GraphQlController {

	@Autowired  private GraphQlExecutor graphQlExecutor;

	@RequestMapping(value="/graphql", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public Object executeOperation(@RequestBody(required = false) Map body) throws IOException {
		return graphQlExecutor.executeRequest(body);
	}


}
