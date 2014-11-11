package org.nextprot.api.web;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.service.TerminologyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Lazy
@Controller
@Api(name = "Terminology", description = "Method to retrieve a complete or partial terminology", role = "rdf")
public class TerminologyController {

	@Autowired private TerminologyService terminologyService;

	@ApiMethod(path = "/rdf/terminology/{term}", verb = ApiVerb.GET, description = "Exports one neXtProt term, this includes: The ontology, the name, the description of the term and its parent.", produces = { "text/turtle"})
	@RequestMapping("/rdf/terminology/{term}")
	public String findOneTerm(
			@ApiParam(name = "term", description = "The accession of a term in a controlled vocabulary. For example, the brain: TS-0095", allowedvalues = { "TS-0095"}) @PathVariable("term") String accession, Model model) {
		model.addAttribute("terminology", this.terminologyService.findTerminologyByAccession(accession));
		model.addAttribute("StringUtils", StringUtils.class);
		return "term";
	}

	
	@ApiMethod(path = "/rdf/terminology/ontology/{ontology}", verb = ApiVerb.GET, description = "Exports the whole neXtProt terminology for the specified ontology, this includes: The ontology, the name, the description and the parent instance.", produces = {"text/turtle"})
	@RequestMapping("/rdf/terminology/ontology/{ontology}")
	public String findAllTermByOntology(
			@ApiParam(name = "ontology", description = "The cv_name in pascal case format of the neXtProt ontology. For example, the 'NextprotAnatomyCv' ontology", allowedvalues = { "NextprotAnatomyCv"}) @PathVariable("ontology") String ontology, Model model) {
		model.addAttribute("termList", this.terminologyService.findTerminologyByOntology(ontology));
		model.addAttribute("StringUtils", StringUtils.class);
		return "term-list";
	}


	@ApiMethod(path = "/rdf/terminology", verb = ApiVerb.GET, description = "Exports the whole neXtProt terminology ordered by the name of the controlled vocabulary, this includes: The ontology, the name, the description and the parent instance.", produces = {"text/turtle"})
	@RequestMapping("/rdf/terminology")
	public String findAllTermOrderedByOntology(Model model) {
		model.addAttribute("termList", this.terminologyService.findAllTerminology());
		model.addAttribute("StringUtils", StringUtils.class);
		return "term-list";
	}
	

}

