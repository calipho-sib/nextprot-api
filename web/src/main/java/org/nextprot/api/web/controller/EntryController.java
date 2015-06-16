package org.nextprot.api.web.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.fluent.FluentEntryService;
import org.nextprot.api.core.utils.NXVelocityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Lazy
@Controller
@Api(name = "Entry", description = "Method to retrieve a complete or partial entry")
public class EntryController {

	@Autowired
	private FluentEntryService fluentEntryService;

    @ModelAttribute
    private void populateModelWithUtilsMethods(Model model) {

        model.addAttribute("StringUtils", StringUtils.class);
        model.addAttribute("NXUtils", NXVelocityUtils.class);
    }

	@ApiMethod(path = "/entry/{entry}", verb = ApiVerb.GET, description = "Exports the whole neXtProt entry, this includes: The overview, the annotations, the keywords, the interactions, the isoforms, the chromosomal location, the genomic mapping, the list of identifiers, the publications, the cross references, the list of peptides, the list of the antibodies and the experimental contexts",
			produces = { MediaType.APPLICATION_XML_VALUE , MediaType.APPLICATION_JSON_VALUE, "text/turtle", /*"text/peff",*/ "text/fasta"})
	@RequestMapping(value = "/entry/{entry}", method = { RequestMethod.GET })
	public String exportEntry(
			@ApiPathParam(name = "entry", description = "The name of the neXtProt entry. For example, the insulin: NX_P01308",  allowedvalues = { "NX_P01308"})
			@PathVariable("entry") String entryName, Model model) {
		
		Entry entry = this.fluentEntryService.newFluentEntry(entryName).buildWithView("entry");
		model.addAttribute("entry", entry);

		return "entry";
	}

	@RequestMapping("/entry/{entryname}/{subpart}")
	public String getSubPart(@PathVariable("entryname") String entryName, @PathVariable("subpart") String subpart, Model model) {
		
		Entry entry = this.fluentEntryService.newFluentEntry(entryName).buildWithView(subpart);
		model.addAttribute("entry", entry);
		return "entry";
	}

	@ApiMethod(path = "/entry/{entry}/isoform", verb = ApiVerb.GET, description = "Gets the isoforms for a given entry", produces = { MediaType.APPLICATION_XML_VALUE , MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping("/entry/{entry}/isoform")
	public String getIsoforms(
			@ApiPathParam(name = "entry", description = "The name of the neXtProt entry. For example, the insulin: NX_P01308",  allowedvalues = { "NX_P01308"}) @PathVariable("entry") String entryName, Model model) {

		Entry entry = this.fluentEntryService.newFluentEntry(entryName).buildWithView("isoform");
		model.addAttribute("entry", entry);
		return "entry";

	}

	@ApiMethod(path = "/entry/{entry}/overview", verb = ApiVerb.GET, description = "Gets an overview of the entry. This includes the protein existence, protein names, gene names, functional region names, cleaved region names, the families, the bio physical and chemical properties and the history. See the Overview object for more details.", produces = { MediaType.APPLICATION_XML_VALUE , MediaType.APPLICATION_JSON_VALUE, "text/turtle"})
	@RequestMapping("/entry/{entry}/overview")
	public String getOverview(
			@ApiPathParam(name = "entry", description = "The name of the neXtProt entry. For example, the insulin: NX_P01308",  allowedvalues = { "NX_P01308"}) @PathVariable("entry") String entryName, Model model) {

		Entry entry = this.fluentEntryService.newFluentEntry(entryName).buildWithView("overview");
		model.addAttribute("entry", entry);
		return "entry";

	}
	
	
	@ApiMethod(path = "/entry/{entry}/antibody", verb = ApiVerb.GET, description = "Gets the list of antibodies for a given entry if any.", produces = { MediaType.APPLICATION_XML_VALUE , MediaType.APPLICATION_JSON_VALUE, "text/turtle"})
	@RequestMapping("/entry/{entry}/antibody")
	public String getAntibodyMapping(
			@ApiPathParam(name = "entry", description = "The name of the neXtProt entry which contains antibodies. For example, insulin: NX_P01308",  allowedvalues = { "NX_P01308"}) @PathVariable("entry") String entryName, Model model) {

		Entry entry = this.fluentEntryService.newFluentEntry(entryName).buildWithView("antibody");
		model.addAttribute("entry", entry);
		return "entry";
	}
	

	@ApiMethod(path = "/entry/{entry}/peptide", verb = ApiVerb.GET, description = "Gets the list of peptides for a given entry", produces = { MediaType.APPLICATION_XML_VALUE , MediaType.APPLICATION_JSON_VALUE, "text/turtle"})
	@RequestMapping("/entry/{entry}/peptide")
	public String getPeptideMapping(
			@ApiPathParam(name = "entry", description = "The name of the neXtProt entry. For example, the insulin: NX_P01308",  allowedvalues = { "NX_P01308"}) @PathVariable("entry") String entryName, Model model) {

		Entry entry = this.fluentEntryService.newFluentEntry(entryName).buildWithView("peptide");
		model.addAttribute("entry", entry);
		return "entry";

	}


	@ApiMethod(path = "/entry/{entry}/srm-peptide", verb = ApiVerb.GET, description = "Gets the list of SRM peptides for a given entry", produces = { MediaType.APPLICATION_XML_VALUE , MediaType.APPLICATION_JSON_VALUE, "text/turtle"})
	@RequestMapping("/entry/{entry}/srm-peptide")
	public String getSrmPeptideMapping(
			@ApiPathParam(name = "entry", description = "The name of the neXtProt entry. For example, the insulin: NX_P01308",  allowedvalues = { "NX_P01308"}) @PathVariable("entry") String entryName, Model model) {
	
		Entry entry = this.fluentEntryService.newFluentEntry(entryName).buildWithView("srm-peptide");
		model.addAttribute("entry", entry);
		return "entry";
		
	}


	@ApiMethod(path = "/entry/{entry}/identifier", verb = ApiVerb.GET, description = "Gets the list of identifiers for a given entry", produces = { MediaType.APPLICATION_XML_VALUE , MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping("/entry/{entry}/identifier")
	public String getIdentifiers(
			@ApiPathParam(name = "entry", description = "The name of the neXtProt entry. For example, the insulin: NX_P01308",  allowedvalues = { "NX_P01308"}) @PathVariable("entry") String entryName, Model model) {

		Entry entry = this.fluentEntryService.newFluentEntry(entryName).buildWithView("identifier");
		model.addAttribute("entry", entry);
		return "entry";
	}
	
	@RequestMapping("/entry/{entry}/chromosomal-location")
	@ApiMethod(path = "/entry/{entry}/chromosomal-location", verb = ApiVerb.GET, description = "Gets the chromosomal locations of a given entry", produces = { MediaType.APPLICATION_XML_VALUE , MediaType.APPLICATION_JSON_VALUE})
	public String getChromosomalLocation(
			@ApiPathParam(name = "entry", description = "The name of the neXtProt entry. For example, the insulin: NX_P01308",  allowedvalues = { "NX_P01308"}) @PathVariable("entry") String entryName, Model model) {
		Entry entry = this.fluentEntryService.newFluentEntry(entryName).buildWithView("chromosomal-location");
		model.addAttribute("entry", entry);
		return "entry";
	}

	@ApiMethod(path = "/entry/{entry}/genomic-mapping", verb = ApiVerb.GET, description = "Gets the genomic mappings for a given entry", produces = { MediaType.APPLICATION_XML_VALUE , MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping("/entry/{entry}/genomic-mapping")
	public String getGenomicMapping(
			@ApiPathParam(name = "entry", description = "The name of the neXtProt entry. For example, the insulin: NX_P01308",  allowedvalues = { "NX_P01308"}) @PathVariable("entry") String entryName, Model model) {
		Entry entry = this.fluentEntryService.newFluentEntry(entryName).buildWithView("genomic-mapping");
		model.addAttribute("entry", entry);
		return "entry";
	}

	@ApiMethod(path = "/entry/{entry}/publication", verb = ApiVerb.GET, description = "Gets the publications of an given entry", produces = { MediaType.APPLICATION_XML_VALUE , MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping("/entry/{entry}/publication")
	public String publications(@ApiPathParam(name = "entry", description = "The name of the neXtProt entry for example, the insulin: NX_P01308",  allowedvalues = { "NX_P01308"}) @PathVariable("entry") String entryName, Model model) {
		Entry entry = this.fluentEntryService.newFluentEntry(entryName).buildWithView("publication");
		model.addAttribute("entry", entry);
		return "entry";
	}
	
	@ApiMethod(path = "/entry/{entry}/xref", verb = ApiVerb.GET, description = "Gets the cross references of a given entry", produces = { MediaType.APPLICATION_XML_VALUE , MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping("/entry/{entry}/xref")
	public String getXrefs(
			@ApiPathParam(name = "entry", description = "The name of the neXtProt entry for example, the insulin: NX_P01308",  allowedvalues = { "NX_P01308"}) 
			@PathVariable("entry") String entryName, Model model) {
		Entry entry = this.fluentEntryService.newFluentEntry(entryName).buildWithView("xref");
		model.addAttribute("entry", entry);
		return "entry";
	}


	@ApiMethod(path = "/entry/{entry}/interaction", verb = ApiVerb.GET, description = "Gets the interactions of a given entry", produces = { MediaType.APPLICATION_XML_VALUE , MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping("/entry/{entry}/interaction")
	public String interactions(
			@ApiPathParam(name = "entry", description = "The name of the neXtProt entry for example: The cytoplasmic tyrosine-protein kinase BMX: NX_P51813", allowedvalues = { "NX_P51813"}) 
			@PathVariable("entry") String entryName, Model model) {
		Entry entry = this.fluentEntryService.newFluentEntry(entryName).buildWithView("interaction");
		model.addAttribute("entry", entry);
		return "entry";
	}

	@ApiMethod(path = "/entry/{entry}/annotation", verb = ApiVerb.GET, description = "Gets the annotations of a given entry grouped by category", produces = { MediaType.APPLICATION_XML_VALUE})
	@RequestMapping("/entry/{entry}/annotation")
	public String getEntryAnnotations(
			@ApiPathParam(name = "entry", description = "The name of the neXtProt entry for example, the insulin: NX_P01308",  allowedvalues = { "NX_P01308"}) 
			@PathVariable("entry") String entryName, Model model) {
		Entry entry = this.fluentEntryService.newFluentEntry(entryName).buildWithView("annotation");
		model.addAttribute("entry", entry);
		return "entry";
	}
	
	@ApiMethod(path = "/entry/{entry}/experimental-context", verb = ApiVerb.GET, description = "Gets the experimental contexts related to the annotations of a given entry", produces = { MediaType.APPLICATION_XML_VALUE , MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping("/entry/{entry}/experimental-context")
	public String getEntryExperimentalContexts(@ApiPathParam(name = "entry", description = "The name of the neXtProt entry for example, the insulin: NX_P01308",  allowedvalues = { "NX_P01308"}) @PathVariable("entry") String entryName, Model model) {
		Entry entry = this.fluentEntryService.newFluentEntry(entryName).buildWithView("experimental-context");
		model.addAttribute("entry", entry);
		return "entry";
	}


}

