package org.nextprot.api.web.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.commons.utils.Tree;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.service.TerminologyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

@Controller
@Api(name = "Terminology", description = "Method to retrieve a terminology")
public class TermController {

	@Autowired private TerminologyService terminolgyService;
	
	@ApiMethod(path = "/terminology-tree/{terminology}", verb = ApiVerb.GET, description = "Gets a terminology", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/terminology-tree/{terminology}", method = { RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Tree<CvTerm>> getTerminologyTree(
			@ApiPathParam(name = "terminology", description = "The name of the terminology. To get a list of possible terminologies, look at terminology-names method",  allowedvalues = { "nextprot-anatomy-cv"})
			@PathVariable("terminology") String terminology) {

		return terminolgyService.findTerminology(TerminologyCv.getTerminologyOf(terminology));
	}

	@ApiMethod(path = "/terminology/{terminology}", verb = ApiVerb.GET, description = "Gets a terminology", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/terminology/{terminology}", method = { RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<CvTerm> getTerminology(
			@ApiPathParam(name = "terminology", description = "The name of the terminology. To get a list of possible terminologies, look at terminology-names method",  allowedvalues = { "nextprot-anatomy-cv"})
			@PathVariable("terminology") String terminology) {

		return terminolgyService.findCvTermsByOntology(TerminologyCv.getTerminologyOf(terminology).name());
	}
	
	
	@ApiMethod(path = "/terminology-names", verb = ApiVerb.GET, description = "Gets a list of terminology names", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/terminology-names", method = { RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<String> getTerminologyNames() {
		return terminolgyService.findTerminologyNamesList();
	}


	// TODO: Not sure about the representation to provide
	@ApiMethod(path = "/ontology/{terminology}", verb = ApiVerb.GET, description = "Gets a terminology", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/ontology/{terminology}", method = { RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, CvTerm> getTerminologyGraph(
			@ApiPathParam(name = "terminology", description = "The name of the terminology. To get a list of possible terminologies, look at terminology-names method",  allowedvalues = { "nextprot-anatomy-cv"})
			@PathVariable("terminology") String terminology) {

		return terminolgyService.findOntologyGraph(TerminologyCv.getTerminologyOf(terminology)).exportMap();
	}
}
