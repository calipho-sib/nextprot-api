package org.nextprot.api.core.controller;

import java.util.List;

import org.jsondoc.core.annotation.ApiParam;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Lazy
@Controller
//@Api(name = "Entry names", description = "Method to retrieve all the entry names")
public class EntryNamesController {

	@Autowired private MasterIdentifierService masterIdentifierService;
	
	/*@ApiMethod(path = "/entry-names/", verb = ApiVerb.GET, description = "Lists the different entry names", produces = { MediaType.APPLICATION_XML_VALUE , MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping("/entry-names")
	@ResponseBody
	public List<String> getEntryNames() {
		return masterIdentifierService.findUniqueNames();
	}*/

	//Check why xml is not allowed
//	@ApiMethod(path = "/entry-names/chromosome/{chromosome}", verb = ApiVerb.GET, description = "Lists the different entry names for a given chromosome", produces = { MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping("/entry-names/chromosome/{chromosome}")
	@ResponseBody
	public List<String> getEntryNamesByChromosome(
			@ApiParam(name = "chromosome", description = "The chromosome number or X / Y. Example: 21", allowedvalues = { "21"}) 
			@PathVariable("chromosome") String chromossome, Model model) {
		return masterIdentifierService.findUniqueNamesOfChromossome(chromossome);
	}

}

