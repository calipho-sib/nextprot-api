package org.nextprot.api.web.controller;

import org.jsondoc.core.annotation.Api;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

@Lazy
@Controller
@Api(name = "Phenotypes (DRAFT)", description = "Method to retrieve phenotypes")
public class PhenotypesController {

	/*@Autowired	private EntryBuilderService entryBuilderService;
	
	@ApiMethod(path = "/phenotypes/{entry}", verb = ApiVerb.GET, description = "",
			produces = {MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping(value = "/phenotypes/{entry}", method = { RequestMethod.GET })
	public String exportPhenotypes(@ApiPathParam(name = "entry", description = "The name of the entry",  allowedvalues = { "NX_P38398"}) @PathVariable("entry") String entryName, Model model) {
		
		Entry entry = this.entryBuilderService.build(EntryConfig.newConfig(entryName).withOverview().withPhenotypes());
		model.addAttribute("entry", entry);

		return "entry"
	}
*/

}

