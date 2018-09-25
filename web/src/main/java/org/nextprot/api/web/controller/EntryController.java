package org.nextprot.api.web.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.*;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.exon.ExonMapping;
import org.nextprot.api.core.export.EntryPartExporterImpl;
import org.nextprot.api.core.export.EntryPartWriterTSV;
import org.nextprot.api.core.service.*;
import org.nextprot.api.core.service.export.format.NextprotMediaType;
import org.nextprot.api.core.service.export.io.SlimIsoformTSVWriter;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.utils.NXVelocityUtils;
import org.nextprot.api.web.service.EntryPageService;
import org.nextprot.api.web.service.impl.writer.JSONObjectsWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
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
	@Autowired private EntryReportStatsService entryReportStatsService;
	@Autowired private IsoformService isoformService;
	@Autowired private MasterIsoformMappingService masterIsoformMappingService;
	@Autowired private EntryGeneReportService entryGeneReportService;
	@Autowired private EntryService entryService;
	@Autowired private EntryExonMappingService entryExonMappingService;

    @ModelAttribute
    private void populateModelWithUtilsMethods(Model model) {

        model.addAttribute("StringUtils", StringUtils.class);
        model.addAttribute("NXUtils", NXVelocityUtils.class);
    }

	@ApiMethod(path = "/entry/{entry}", verb = ApiVerb.GET, description = "Exports the whole neXtProt entry, this includes: The overview, the annotations, the keywords, the interactions, the isoforms, the chromosomal location, the genomic mapping, the list of identifiers, the publications, the cross references, the list of peptides, the list of the antibodies and the experimental contexts",
			produces = { MediaType.APPLICATION_XML_VALUE , MediaType.APPLICATION_JSON_VALUE, NextprotMediaType.TURTLE_MEDIATYPE_VALUE, NextprotMediaType.FASTA_MEDIATYPE_VALUE})
	@RequestMapping(value = "/entry/{entry}", method = { RequestMethod.GET })
	public String exportEntry(
			@ApiPathParam(name = "entry", description = "The name of the neXtProt entry. For example, the insulin: NX_P01308",  allowedvalues = { "NX_P01308"})
			@PathVariable("entry") String entryName,
			@RequestParam(value = "term-child-of", required = false) String ancestorTerm,
			@RequestParam(value = "property-name", required = false) String propertyName,
			@RequestParam(value = "property-value", required = false) String propertyValue,
			HttpServletRequest request,
			Model model) {

		boolean bed = (request.getParameter("bed") == null) ? true : Boolean.valueOf(request.getParameter("bed"));

		Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryName).withEverything().withBed(bed));

		if (ancestorTerm != null || propertyName != null) {
			filterEntryAnnotations(entry, ancestorTerm, propertyName, propertyValue);
		}
		model.addAttribute("entry", entry);

		return "entry";
	}

	@RequestMapping(value = "/entry/{entry}/{blockOrSubpart}", method = { RequestMethod.GET })
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
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

			    EntryPartWriterTSV writer = new EntryPartWriterTSV(EntryPartExporterImpl.fromSubPart(blockOrSubpart), baos);
				writer.write(entry);

				model.addAttribute("tsv", baos.toString(StandardCharsets.UTF_8.name()));
				baos.close();
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
	public List<EntryReport> getGeneEntryReport(
			@ApiPathParam(name = "entry", description = "The name of the neXtProt entry. For example, the insulin: NX_P01308",  allowedvalues = { "NX_P01308"})
			@PathVariable("entry") String entryName) {

		return entryGeneReportService.reportEntry(entryName).stream()
				.sorted(new EntryReport.ByChromosomeComparator().thenComparing(EntryReport.newByChromosomalPositionComparator()))
				.collect(Collectors.toList());
	}

	@ApiMethod(path = "/isoforms", verb = ApiVerb.GET, description = "Retrieves all isoforms", produces = {MediaType.APPLICATION_JSON_VALUE, NextprotMediaType.TSV_MEDIATYPE_VALUE})
	@RequestMapping(value = "/isoforms", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE, NextprotMediaType.TSV_MEDIATYPE_VALUE} )
	public void getListOfIsoformAcMd5Sequence(HttpServletRequest request, HttpServletResponse response) {

		NextprotMediaType mediaType = NextprotMediaType.valueOf(request);

		try {
			List<SlimIsoform> isoforms = isoformService.findListOfIsoformAcMd5Sequence();

			if (mediaType == NextprotMediaType.JSON) {

				JSONObjectsWriter<SlimIsoform> writer = new JSONObjectsWriter<>(response.getOutputStream());
				writer.write(isoforms);
			}
			else if (mediaType == NextprotMediaType.TSV) {

				SlimIsoformTSVWriter writer = new SlimIsoformTSVWriter(response.getOutputStream());
				writer.write(isoforms);
				writer.close();
			}
		} catch (IOException e) {
			throw new NextProtException("cannot get isoforms in "+mediaType.getExtension()+" format", e);
		}
	}

	@RequestMapping(value = "/entry/{entry}/isoform/mapping", produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public List<IsoformSpecificity> getIsoformsMappings(@PathVariable("entry") String entryName) {
		return masterIsoformMappingService.findMasterIsoformMappingByEntryName(entryName);
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
	 * Hidden service reporting page displayability used by nextprot ui
	 * @param entryName the nextprot accession number
	 * @return a map of page label to boolean
	 */
	@RequestMapping(value = "/entry/{entry}/page-display", method = { RequestMethod.GET }, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public Map<String, Boolean> testPageDisplay(@PathVariable("entry") String entryName) {

		return entryPageService.hasContentForPageDisplay(entryName);
	}

	@RequestMapping(value = "/page-view/{view}/{entry}/xref", method = { RequestMethod.GET }, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
	public Map<String, Object> getEntryPageViewXref(@PathVariable("view") String viewName, @PathVariable("entry") String entryName) {

	    Map<String, Object> map = new HashMap<>();

	    Entry entry = new Entry(entryName);
        entry.setXrefs(entryPageService.extractXrefForPageView(entryName, viewName));

        map.put("entry", entry);

		return map;
	}

	@ApiMethod(path = "/entry/{entry}/stats", verb = ApiVerb.GET, description = "Reports neXtProt entry stats", produces = { MediaType.APPLICATION_JSON_VALUE } )
	@RequestMapping(value = "/entry/{entry}/stats", method = { RequestMethod.GET }, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public EntryReportStats getEntryReportStats(
			@ApiPathParam(name = "entry", description = "The name of the neXtProt entry. For example, the insulin: NX_P01308",  allowedvalues = { "NX_P01308"})
			@PathVariable("entry") String entryName) {

		return entryReportStatsService.reportEntryStats(entryName);
	}

	@ApiMethod(path = "/isoform/{accession}", verb = ApiVerb.GET, description = "Exports a neXtProt isoform",
			produces = { NextprotMediaType.FASTA_MEDIATYPE_VALUE})
	@RequestMapping(value = "/isoform/{accession}", method = { RequestMethod.GET })
	public String exportIsoform(
			@ApiPathParam(name = "accession", description = "The name of the neXtProt isoform. For example, the insulin: NX_P01308-1",  allowedvalues = { "NX_P01308-1"})
			@PathVariable("accession") String isoformAccession, Model model) {

		String entryAccession = entryService.findEntryAccessionFromIsoformAccession(isoformAccession);

		Isoform isoform = isoformService.findIsoformByName(entryAccession, isoformAccession);

		Entry entry = entryBuilderService.build(EntryConfig.newConfig(entryAccession).withTargetIsoforms().withOverview());

		model.addAttribute("entry", entry);
		model.addAttribute("isoform", isoform);

		return "isoform";
	}

	//@ApiMethod(path = "/entry/{entry}/exon-mapping", verb = ApiVerb.GET, description = "Find the exon mappings of a neXtProt entry", produces = { MediaType.APPLICATION_JSON_VALUE })
	@RequestMapping(value = "/entry/{entry}/exon-mapping", method = { RequestMethod.GET })
	@ResponseBody
	public ExonMapping findExonsByIsoformByShorterENST(
			@ApiPathParam(name = "entry", description = "The name of the neXtProt entry. For example, the insulin: NX_P01308",  allowedvalues = { "NX_P01308"})
			@PathVariable("entry") String entryName) {

		return entryExonMappingService.findExonMappingGeneXIsoformXShorterENST(entryName);
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

