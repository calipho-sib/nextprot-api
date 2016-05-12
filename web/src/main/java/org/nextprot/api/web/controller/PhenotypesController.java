package org.nextprot.api.web.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Lazy
@Controller
@Api(name = "Phenotypes (DRAFT)", description = "Method to retrieve phenotypes")
public class PhenotypesController {
	@Autowired	private EntryBuilderService entryBuilderService;
	
	@ApiMethod(path = "/phenotypes/{entry}", verb = ApiVerb.GET, description = "",
			produces = {MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping(value = "/phenotypes/{entry}", method = { RequestMethod.GET })
	public String exportPhenotypes(@ApiPathParam(name = "entry", description = "The name of the entry",  allowedvalues = { "NX_P38398"}) @PathVariable("entry") String entryName, Model model) {
		
		Entry entry = this.entryBuilderService.build(EntryConfig.newConfig(entryName).withOverview().withPhenotypes());
		model.addAttribute("entry", entry);

		return "entry";
	}


}

