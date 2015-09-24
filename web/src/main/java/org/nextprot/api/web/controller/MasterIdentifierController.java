package org.nextprot.api.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@Api(name = "Master Identifiers", description = "Retrieves nextProt idenfitiers")
public class MasterIdentifierController {

	@Autowired	private MasterIdentifierService masterIdentifierService;

	@ApiMethod(path = "/master-identifiers", verb = ApiVerb.GET, description = "Retrieve the identifiers for all neXtProt entries", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/master-identifiers", method = { RequestMethod.GET })
	public List<String> masterIdentifiers() {
		return new ArrayList<String>(masterIdentifierService.findUniqueNames());
	}
	
	
	@ApiMethod(path = "/master-identifiers/chromosome/{chromosome}", verb = ApiVerb.GET, description = "Retrieve the identifiers for all neXtProt entries", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/master-identifiers/chromosome/{chromosome}", method = { RequestMethod.GET })
	public List<String> masterIdentifiersPerChromosome(
			@ApiPathParam(name = "chromosome", description = "The chromosome number or name (X,Y..)",  allowedvalues = { "master-isoform-mapping"}) @PathVariable("chromosome")  String chromosome) {
		return new ArrayList<String>(masterIdentifierService.findUniqueNamesOfChromosome(chromosome));
	}

}
