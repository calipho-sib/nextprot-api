package org.nextprot.api.web.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.EntryReport;
import org.nextprot.api.core.domain.IsoformSequenceInfoPeff;
import org.nextprot.api.core.domain.IsoformSpecificity;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.*;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.utils.NXVelocityUtils;
import org.nextprot.api.core.utils.annot.export.EntryPartExporterImpl;
import org.nextprot.api.core.utils.annot.export.EntryPartWriterTSV;
import org.nextprot.api.web.service.EntryPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Lazy
@Controller
@Api(name = "Entry", description = "Method to retrieve a complete or partial entry")
public class EntryController {

	@Autowired private EntryBuilderService entryBuilderService;
	@Autowired private EntryPageService entryPageService;
	@Autowired private AnnotationService annotationService;
	@Autowired private EntryReportService entryReportService;
	@Autowired private PeffService peffService;
	@Autowired private MasterIsoformMappingService masterIsoformMappingService;

    @ModelAttribute
    private void populateModelWithUtilsMethods(Model model) {

        model.addAttribute("StringUtils", StringUtils.class);
        model.addAttribute("NXUtils", NXVelocityUtils.class);
    }

	@ApiMethod(path = "/entry/{entry}", verb = ApiVerb.GET, description = "Exports the whole neXtProt entry, this includes: The overview, the annotations, the keywords, the interactions, the isoforms, the chromosomal location, the genomic mapping, the list of identifiers, the publications, the cross references, the list of peptides, the list of the antibodies and the experimental contexts",
			produces = { MediaType.APPLICATION_XML_VALUE , MediaType.APPLICATION_JSON_VALUE, "text/turtle", "text/peff", "text/fasta"})
	@RequestMapping(value = "/entry/{entry}", method = { RequestMethod.GET })
	public String exportEntry(
			@ApiPathParam(name = "entry", description = "The name of the neXtProt entry. For example, the insulin: NX_P01308",  allowedvalues = { "NX_P01308"})
			@PathVariable("entry") String entryName,
			@RequestParam(value = "term-child-of", required = false) String ancestorTerm,
			@RequestParam(value = "property-name", required = false) String propertyName,
			@RequestParam(value = "property-value", required = false) String propertyValue,
			HttpServletRequest request,
			Model model) {

    	Entry entry;

		if (request.getRequestURI().toLowerCase().endsWith(".peff")) {

			entry = entryBuilderService.build(EntryConfig.newConfig(entryName).withTargetIsoforms());
			model.addAttribute("peffByIsoform", entryReportService.reportIsoformPeffHeaders(entryName));
		}
		else {
			boolean bed = (request.getParameter("bed") == null) ? true : Boolean.valueOf(request.getParameter("bed"));

			entry = entryBuilderService.build(EntryConfig.newConfig(entryName).withEverything().withBed(bed));

			if (ancestorTerm != null || propertyName != null) {
				filterEntryAnnotations(entry, ancestorTerm, propertyName, propertyValue);
			}
		}

		model.addAttribute("entry", entry);

		return "entry";
	}

	@RequestMapping("/entry/{entry}/{blockOrSubpart}")
	public String getSubPart(
			@PathVariable("entry") String entryName,
			@PathVariable("blockOrSubpart") String blockOrSubpart,
			@RequestParam(value = "term-child-of", required = false) String ancestorTerm,
			@RequestParam(value = "property-name", required = false) String propertyName,
			@RequestParam(value = "property-value", required = false) String propertyValueOrAccession,
			
			HttpServletRequest request, Model model) {

    	boolean goldOnly = "true".equalsIgnoreCase(request.getParameter("goldOnly"));
    	boolean bed = null==request.getParameter("bed") ? true: Boolean.valueOf(request.getParameter("bed"));

		Entry entry = this.entryBuilderService.build(EntryConfig.newConfig(entryName).with(blockOrSubpart).withGoldOnly(goldOnly).withBed(bed));

		if (ancestorTerm != null || propertyName != null) {
			filterEntryAnnotations(entry, ancestorTerm, propertyName, propertyValueOrAccession);
		}

		if (request.getRequestURI().toLowerCase().endsWith(".tsv")) {
			try {
				EntryPartWriterTSV writer = new EntryPartWriterTSV(EntryPartExporterImpl.fromSubPart(blockOrSubpart));
				writer.write(entry);

				model.addAttribute("tsv", writer.getOutputString());
			} catch (IOException e) {
				throw new NextProtException("cannot export "+entryName+" "+blockOrSubpart+" in tsv format", e);
			}
		}

		model.addAttribute("entry", entry);
		return "entry";
	}

	@ApiMethod(path = "/entry/{entry}/report", verb = ApiVerb.GET, description = "Reports neXtProt entry informations", produces = { MediaType.APPLICATION_JSON_VALUE } )
	@RequestMapping(value = "/entry/{entry}/report", method = { RequestMethod.GET }, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public List<EntryReport> getEntryReport(
			@ApiPathParam(name = "entry", description = "The name of the neXtProt entry. For example, the insulin: NX_P01308",  allowedvalues = { "NX_P01308"})
			@PathVariable("entry") String entryName) {

		return entryReportService.reportEntry(entryName).stream()
				.sorted(new EntryReport.ByChromosomeComparator().thenComparing(EntryReport.newByChromosomalPositionComparator()))
				.collect(Collectors.toList());
	}

	@ApiMethod(path = "/isoform/{accession}/peff", verb = ApiVerb.GET, description = "Get isoform sequence informations", produces = { MediaType.APPLICATION_JSON_VALUE } )
	@RequestMapping(value = "/isoform/{accession}/peff", method = { RequestMethod.GET }, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public IsoformSequenceInfoPeff getIsoformSequenceInfos(
			@ApiPathParam(name = "accession", description = "The neXtProt isoform accession. For example, the first isoform of insulin: NX_P01308-1",  allowedvalues = { "NX_P01308-1"})
			@PathVariable("accession") String isoformAccession) {

		return peffService.formatSequenceInfo(isoformAccession);
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
	@RequestMapping(value = "/entry/{entry}/page-display", method = { RequestMethod.GET }, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public Map<String, Boolean> testPageDisplay(@PathVariable("entry") String entryName) {

		return entryPageService.testEntryContentForPageDisplay(entryName);
	}

	/**
	 * Hidden service reporting the number of annotations contained for the specific entry
	 * @param entryName the nextprot accession number
	 * @return the annotation count
	 */
	@RequestMapping(value = "/entry/{entry}/annotation-count", method = { RequestMethod.GET }, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public Integer countAnnotation(@PathVariable("entry") String entryName) {

		return this.entryBuilderService.build(EntryConfig.newConfig(entryName).with("annotation")).getAnnotations().size();
	}

	/**
	 * Filter entry annotations
	 * @param entry the entry to update
	 * @param ancestorCvTerm the ancestor term
	 * @param propertyName property name
	 * @param propertyValueOrAccession property value or accession (ignored if property name is null)
	 */
	private void filterEntryAnnotations(Entry entry, String ancestorCvTerm, String propertyName, String propertyValueOrAccession) {

		final Predicate<Annotation> cvTermPredicate = (ancestorCvTerm != null) ?
				annotationService.createDescendantTermPredicate(ancestorCvTerm) :
				annotation -> true;

		final Predicate<Annotation> propertyPredicate = (propertyName != null) ?
				annotationService.buildPropertyPredicate(propertyName, propertyValueOrAccession) :
				annotation -> true;

		entry.setAnnotations(entry.getAnnotations().stream()
				.filter(cvTermPredicate.and(propertyPredicate))
				.collect(Collectors.toList()));
	}
}

