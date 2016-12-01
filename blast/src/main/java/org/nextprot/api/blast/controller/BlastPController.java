package org.nextprot.api.blast.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.blast.domain.BlastPConfig;
import org.nextprot.api.blast.service.BlastPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@Api(name = "BlastP", description = "Search protein sequence into neXtProt database.", group = "Tools")
public class BlastPController {

	@Autowired
	private BlastPService blastPService;

	@ApiMethod(path = "/blastp/{query}", verb = ApiVerb.GET, description = "Search protein sequence", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/blastp/{query}", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public String runBlastp(
			@ApiPathParam(name = "query", description = "A protein sequence query.",  allowedvalues = { "GTTYVTDKSEEDNEIESEEEVQPKTQGSRR" })
			@PathVariable("query") String query,
			@RequestParam(value = "debug", required = false) boolean debug) {

		// TODO: get the following paths from properties
		BlastPConfig config = new BlastPConfig("/Users/fnikitin/Applications/ncbi-blast-2.3.0+/bin", "/Users/fnikitin/data/blast/db");
		config.setDebugMode(debug);

		return blastPService.runBlastP(config, query);
	}
}