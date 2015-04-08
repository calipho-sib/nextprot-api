package org.nextprot.api.web.controller;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

@Lazy
@Controller
//@Api(name = "Reference", description = "Method to retrieve xrefs")
public class ReferenceController {

	/**

	@Autowired private DbXrefService xrefService;

	@ApiMethod(path = "/rdf/reference/{accession}", verb = ApiVerb.GET, description = "Exports one neXtProt reference.", produces = {  "text/turtle"})
	@RequestMapping("/rdf/reference/{accession}")
	public String findOnePublication(
			@ApiParam(name = "accession", description = "The accession of the neXtProt reference. For example, 10.1073/pnas.0805139105", allowedvalues = { "10.1073/pnas.0805139105"}) @PathVariable("accession") String accession, Model model) {
		model.addAttribute("reference", this.xrefService.findDbXrefByAccession(accession));
		model.addAttribute("prefix", true);
		model.addAttribute("StringUtils", StringUtils.class);
		return "xref";
	}


	@ApiMethod(path = "/rdf/reference", verb = ApiVerb.GET, description = "Exports the whole neXtProt reference ordered by year and title.", produces = { "text/turtle"})
	@RequestMapping("/rdf/reference")
	public String findAllPublication(Model model) {
		model.addAttribute("referenceList", this.xrefService.findAllDbXrefs());
		model.addAttribute("StringUtils", StringUtils.class);
		return "xref-list";
	}
	
	*/

}

