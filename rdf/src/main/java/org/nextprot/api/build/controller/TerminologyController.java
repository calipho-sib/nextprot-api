package org.nextprot.api.build.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
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
@Api(name = "Terminology (RDF)", description = "Method to retrieve terminologies", group="Build rdf")
public class TerminologyController {

	@Autowired private TerminologyService terminologyService;

	@ApiMethod(path = "/rdf/terminology/{term}", verb = ApiVerb.GET, description = "Exports one neXtProt term, this includes: The ontology, the name, the description of the term and its parent.", produces = { "text/turtle"})
	@RequestMapping("/rdf/terminology/{term}")
	public String findOneTerm(
			@PathVariable("term") String accession, Model model) {
		model.addAttribute("terminology", this.terminologyService.findCvTermByAccessionOrThrowRuntimeException(accession));
		model.addAttribute("StringUtils", StringUtils.class);
		return "term";
	}

	
	@ApiMethod(path = "/rdf/terminology/ontology/{ontology}", verb = ApiVerb.GET, description = "Exports the whole neXtProt terminology for the specified ontology, this includes: The ontology, the name, the description and the parent instance.", produces = {"text/turtle"})
	@RequestMapping("/rdf/terminology/ontology/{ontology}")
	public String findAllTermByOntology(
			@PathVariable("ontology") String ontology, Model model) {
		model.addAttribute("termList", this.terminologyService.findCvTermsByOntology(ontology));
		model.addAttribute("StringUtils", StringUtils.class);
		return "term-list";
	}


	@ApiMethod(path = "/rdf/terminology", verb = ApiVerb.GET, description = "Exports the whole neXtProt terminology ordered by the name of the controlled vocabulary, this includes: The ontology, the name, the description and the parent instance.", produces = {"text/turtle"})
	@RequestMapping("/rdf/terminology")
	public String findAllTermOrderedByOntology(Model model) {
		model.addAttribute("termList", this.terminologyService.findAllCVTerms());
		model.addAttribute("StringUtils", StringUtils.class);
		return "term-list";
	}
	

}

