package org.nextprot.api.web;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.service.InteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Lazy
@Controller
@Api(name = "Interaction", description = "Method to retrieve the list of existing interactions")
public class InteractionController {


	@Autowired private InteractionService service;

	@ApiMethod(path = "/rdf/interaction", verb = ApiVerb.GET, description = "Exports full list of interactions", produces = { "text/turtle"})
	@RequestMapping("/rdf/interaction")
	public String findAllExperimentalContexts(Model model) {
		model.addAttribute("interactionList", this.service.findAllInteractions());
		model.addAttribute("StringUtils", StringUtils.class);
		return "interaction-list";
	}
	

}
