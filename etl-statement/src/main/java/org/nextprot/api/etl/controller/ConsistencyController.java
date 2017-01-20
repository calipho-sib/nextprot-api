package org.nextprot.api.etl.controller;

import java.util.List;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.etl.service.ConsistencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Api(name = "Consistency", description = "Checks the consistency of publications and cv terms", group="ETL")
@RequestMapping(value = "/consistency", method = { RequestMethod.GET }, produces = { MediaType.APPLICATION_JSON_VALUE })
public class ConsistencyController {

	@Autowired
	private ConsistencyService consistencyService;

	@ApiMethod(path = "/missing-pubmed-ids", verb = ApiVerb.GET, description = "Return a list of missing publications. When consistent, the list should be empty.", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/missing-pubmed-ids/", method = { RequestMethod.GET }, produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public List<String> checkConsistencyOfPublications() {
		return consistencyService.findMissingPublications();
	}
	
	
	@ApiMethod(path = "/missing-cvterms", verb = ApiVerb.GET, description = "Return a list of missing cv term. When consistent, the list should be empty.", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/missing-cvterms/", method = { RequestMethod.GET }, produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public List<String> checkConsistencyOfCvTerms() {
		return consistencyService.findMissingCvTerms();
	}

}
