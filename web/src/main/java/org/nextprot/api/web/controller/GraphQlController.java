package org.nextprot.api.web.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.utils.graph.CvTermGraph;
import org.nextprot.api.web.service.GraphQlExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Controller
@Api(name = "GraphQlController", description = "Method to retrieve data using graphql syntax")
public class GraphQlController {

	@Autowired  private GraphQlExecutor graphQlExecutor;

	@ResponseBody
	public Object executeOperation(@RequestBody Map body) {
		long startTime = System.currentTimeMillis();
		String uuid = UUID.randomUUID().toString();

		//log.debug("Start processing graphQL request {}", uuid);
		Object requestResult = graphQlExecutor.executeRequest(body);

		//log.debug("Finished processing graphQL request {} in {} ms", uuid, System.currentTimeMillis() - startTime);

		return requestResult;
	}

}
