package org.nextprot.api.web.controller;

import java.util.List;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.commons.utils.Tree;
import org.nextprot.api.core.domain.Terminology;
import org.nextprot.api.core.service.TerminologyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Api(name = "Terminology", description = "Method to retrieve a terminology")
public class TermController {

	@Autowired private TerminologyService terminolgyService;
	
	@ApiMethod(path = "/terminology/{terminology}", verb = ApiVerb.GET, description = "Gets a terminology", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/terminology/{terminology}", method = { RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Tree<Terminology>> getTerminology(
			@ApiPathParam(name = "terminology", description = "The name of the terminology",  allowedvalues = { "nextprot-anatomy-cv"})
			@PathVariable("terminology") String terminology ,

			@ApiPathParam(name = "maxDepth", description = "The max depth",  allowedvalues = { "1"})
		@RequestParam(value = "maxDepth", required = false) Integer maxDepth) {

		return terminolgyService.findTerminologyTreeList(TerminologyCv.getTerminologyOf(terminology), (maxDepth == null) ? 100 : Integer.valueOf(maxDepth));
	}
	
	
	@ApiMethod(path = "/terminology-names", verb = ApiVerb.GET, description = "Gets a list of terminology names", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/terminology-names", method = { RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<String> getTerminologyNames() {
		return terminolgyService.findTerminologyNamesList();
	}
	
}