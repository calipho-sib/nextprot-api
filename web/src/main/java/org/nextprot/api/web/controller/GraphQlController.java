package org.nextprot.api.web.controller;

import net.sf.ehcache.constructs.web.PageInfo;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsondoc.core.annotation.Api;
import org.nextprot.api.web.service.GraphQlExecutor;
import org.nextprot.api.web.utils.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@Api(name = "GraphQlController", description = "Method to retrieve data using graphql syntax")
public class GraphQlController {

	@Autowired  private GraphQlExecutor graphQlExecutor;
	@Autowired private ServletContext servletContext;

	@RequestMapping(value="/graphql", method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public Object executeOperation(@RequestBody(required = false) Map body) throws IOException {
		return graphQlExecutor.executeRequest(body);
	}

	@RequestMapping(value="/graphiql", method={RequestMethod.GET})
	protected ModelAndView graphiqlIntercase(HttpServletRequest request, HttpServletResponse response) throws Exception {
		WebUtils.writeHtmlContent("graphiql.html", response, servletContext);
		return null;
	}


}
