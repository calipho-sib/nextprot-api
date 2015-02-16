package org.nextprot.api.web;

import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.service.ExperimentalContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Lazy
@Controller
//@Api(name = "ExperimentalContext", description = "Method to retrieve the list of existing experimental contexts")
public class ExperimentalContextController {


	@Autowired private ExperimentalContextService ecService;

	@ApiMethod(path = "/rdf/experimentalcontext", verb = ApiVerb.GET, description = "Exports full list of experimental contexts", produces = { "text/turtle"})
	@RequestMapping("/rdf/experimentalcontext")
	public String findAllExperimentalContexts(Model model) {
		model.addAttribute("experimentalContextList", this.ecService.findAllExperimentalContexts());
		model.addAttribute("StringUtils", StringUtils.class);
		return "experimental-context-list";
	}
	

}

