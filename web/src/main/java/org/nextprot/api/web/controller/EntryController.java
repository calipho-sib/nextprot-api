package org.nextprot.api.web.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.IsoformSpecificity;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.MasterIsoformMappingService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.utils.NXVelocityUtils;
import org.nextprot.api.web.service.EntryPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Lazy
@Controller
@Api(name = "Entry", description = "Method to retrieve a complete or partial entry")
public class EntryController {

	@Autowired private EntryBuilderService entryBuilderService;
	@Autowired private MasterIsoformMappingService masterIsoformMappingService;
	@Autowired private EntryPageService entryPageService;
	@Autowired private AnnotationService annotationService;

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
			@PathVariable("entry") String entryName,
			@RequestParam(value = "term-child-of", required = false) String ancestorTerm, Model model) {
		
		Entry entry = this.entryBuilderService.build(EntryConfig.newConfig(entryName).withEverything());

		// filter enabled
		if (ancestorTerm != null && !ancestorTerm.isEmpty()) {

			entry.setAnnotations(annotationService.filterByCvTermAncestor(entry.getAnnotations(), ancestorTerm));
		}

		model.addAttribute("entry", entry);

		return "entry";
	}

	@RequestMapping("/entry/{entry}/{blockOrSubpart}")
	public String getSubPart(@PathVariable("entry") String entryName,
							 @PathVariable("blockOrSubpart") String blockOrSubpart,
							 @RequestParam(value = "term-child-of", required = false) String ancestorTerm,
							 HttpServletRequest request, Model model) {
		//example:
		//    http://localhost:8080/entry/NX_P01308/go-molecular-function.json?term-child-of=GO:0005102
		// or http://localhost:8080/entry/NX_P01308/go-molecular-function.json
		boolean goldOnly = "true".equalsIgnoreCase(request.getParameter("goldOnly"));

		Entry entry = this.entryBuilderService.build(EntryConfig.newConfig(entryName).with(blockOrSubpart).withGoldOnly(goldOnly));

		// filter enabled
		if (ancestorTerm != null && !ancestorTerm.isEmpty()) {

			entry.setAnnotations(annotationService.filterByCvTermAncestor(entry.getAnnotations(), ancestorTerm));
		}

		model.addAttribute("entry", entry);
		return "entry";
	}

	@RequestMapping(value = "/entry/{entry}/isoform/mapping", produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public List<IsoformSpecificity> getIsoformsMappings(@PathVariable("entry") String entryName) {
		return masterIsoformMappingService.findMasterIsoformMappingByEntryName(entryName);
	}

	/**
	 * Hidden service reporting page displayability used by nextprot ui
	 * @param entryName the nextprot accession number
	 * @return a map of page label to boolean
	 */
	@RequestMapping(value = "/entry/{entry}/page-display", method = { RequestMethod.GET })
	@ResponseBody
	public Map<String, Boolean> testPageDisplay(@PathVariable("entry") String entryName) {

		return entryPageService.testEntryContentForPageDisplay(entryName);
	}

	/**
	 * Hidden service reporting the number of annotations contained for the specific entry
	 * @param entryName the nextprot accession number
	 * @return the annotation count
	 */
	@RequestMapping(value = "/entry/{entry}/annotation-count", method = { RequestMethod.GET })
	@ResponseBody
	public Integer countAnnotation(@PathVariable("entry") String entryName) {

		return this.entryBuilderService.build(EntryConfig.newConfig(entryName).with("annotation")).getAnnotations().size();
	}
}

