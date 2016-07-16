package org.nextprot.api.etl.statement.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.etl.statement.service.StatementETLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@Api(name = "Statement Extract Transform and Load", description = "Methods to check and map features over isoforms")
public class StatementETLController {

	@Autowired
	StatementETLService statementSourceCollectorAndLoaderService;

	@ApiMethod(path = "/statements/load/{source}", verb = ApiVerb.GET, description = "Validate isoform feature", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/statements/{source}", method = { RequestMethod.GET }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public String loadStatements(@ApiPathParam(name = "source", description = "The source to load from", allowedvalues = { "bioeditor" }) @PathVariable("category") String source) {

		return statementSourceCollectorAndLoaderService.etlStatements(source);

	}

}
