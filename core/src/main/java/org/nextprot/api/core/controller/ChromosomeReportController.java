package org.nextprot.api.core.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.ChromosomeReport;
import org.nextprot.api.core.service.ChromosomeReportExportService;
import org.nextprot.api.core.service.ChromosomeReportService;
import org.nextprot.api.core.service.export.format.NextprotMediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

@Controller
@Api(name = "Chromosome reports", description = "Reports statistics about entries on chromosome")
public class ChromosomeReportController {

	@Autowired
	private ChromosomeReportService chromosomeReportService;

	@Autowired
	private ChromosomeReportExportService chromosomeReportExportService;

	@ApiMethod(path = "/chromosomes", verb = ApiVerb.GET, description = "Get the list of chromosome names referenced in neXtProt",
			produces = { MediaType.APPLICATION_JSON_VALUE } )
	@RequestMapping(value = "/chromosomes", method = {RequestMethod.GET})
	@ResponseBody
	public List<String> getChromosomeNames() {

		return ChromosomeReportService.getChromosomeNames();
	}

	@ApiMethod(path = "/chromosome-reports/summary", verb = ApiVerb.GET, description = "Get chromosomes referenced in neXtProt with count statistics",
			produces = { MediaType.APPLICATION_JSON_VALUE } )
	@RequestMapping(value = "/chromosome-reports/summary", method = {RequestMethod.GET})
	@ResponseBody
	public Map<String, ChromosomeReport.Summary> getChromosomeSummaries() {

		return chromosomeReportService.getChromosomeSummaries();
	}

	@ApiMethod(path = "/chromosome-reports/count-by-pe", verb = ApiVerb.GET, description = "Count number of entries grouped by protein evidences for all chromosomes",
			produces = { MediaType.APPLICATION_JSON_VALUE } )
	@RequestMapping(value = "/chromosome-reports/count-by-pe", method = {RequestMethod.GET})
	@ResponseBody
	public Map<String, ChromosomeReport.EntryCountByProteinEvidence> getChromosomeEntryCountByProteinEvidence() {

		return chromosomeReportService.getChromosomeEntryCountByProteinEvidence();
	}

	@ApiMethod(path = "/chromosome-report/{chromosome}", verb = ApiVerb.GET, description = "Report informations of neXtProt entries coming from genes located on a given chromosome",
			produces = { MediaType.APPLICATION_JSON_VALUE } )
	@RequestMapping(value = "/chromosome-report/{chromosome}", method = {RequestMethod.GET})
	@ResponseBody
	public ChromosomeReport reportChromosome(
			@ApiPathParam(name = "chromosome", description = "The chromosome number or name (X,Y..)",  allowedvalues = { "Y"})
			@PathVariable("chromosome")  String chromosome) {

		return chromosomeReportService.reportChromosome(chromosome);
	}

	@ApiMethod(path = "/chromosome-report/{chromosome}/summary", verb = ApiVerb.GET, description = "Report summary of neXtProt entries coming from genes located on a given chromosome",
    		produces = { MediaType.APPLICATION_JSON_VALUE } )
	@RequestMapping(value = "/chromosome-report/{chromosome}/summary", method = {RequestMethod.GET})
	@ResponseBody
	public ChromosomeReport.Summary reportChromosomeSummary(
            @ApiPathParam(name = "chromosome", description = "The chromosome number or name (X,Y..)",  allowedvalues = { "Y"})
			@PathVariable("chromosome")  String chromosome) {

		return chromosomeReportService.reportChromosome(chromosome).getSummary();
	}

	@ApiMethod(path = "/chromosome-report/{chromosome}/count-by-pe", verb = ApiVerb.GET, description = "Report number of entries grouped by protein evidences for a given chromosome",
			produces = { MediaType.APPLICATION_JSON_VALUE } )
	@RequestMapping(value = "/chromosome-report/{chromosome}/count-by-pe", method = {RequestMethod.GET})
	@ResponseBody
	public ChromosomeReport.EntryCountByProteinEvidence reportChromosomeEntryByProteinEvidence(
			@ApiPathParam(name = "chromosome", description = "The chromosome number or name (X,Y..)",  allowedvalues = { "Y"})
			@PathVariable("chromosome")  String chromosome) {

		return chromosomeReportService.reportChromosome(chromosome).getEntryCountByProteinEvidence();
	}

	@ApiMethod(path = "/chromosome-report/export/{chromosome}", verb = ApiVerb.GET, description = "Export informations of neXtProt entries located on a given chromosome",
			produces = { MediaType.TEXT_PLAIN_VALUE, NextprotMediaType.TSV_MEDIATYPE_VALUE } )
	@RequestMapping(value = "/chromosome-report/export/{chromosome}", method = {RequestMethod.GET})
	public void exportChromosomeEntriesReport(
			@ApiPathParam(name = "chromosome", description = "The chromosome number or name (X,Y..)",  allowedvalues = { "Y"})
			@PathVariable("chromosome")  String chromosome, HttpServletRequest request, HttpServletResponse response) {

		NextprotMediaType mediaType = NextprotMediaType.valueOf(request);

		try (OutputStream os = response.getOutputStream()) {

			String filename = "nextprot_chromosome_" + chromosome + "." + mediaType.getExtension();
			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
			chromosomeReportExportService.exportChromosomeEntryReport(chromosome, NextprotMediaType.valueOf(request), os);
		}
		catch (IOException e) {
			throw new NextProtException(e.getMessage()+": cannot export chromosome "+chromosome+" as "+ mediaType);
		}
	}

	@ApiMethod(path = "/chromosome-report/export/hpp/{chromosome}", verb = ApiVerb.GET, description = "Export informations of neXtProt entries located on a given chromosome by accession",
			produces = { MediaType.TEXT_PLAIN_VALUE, NextprotMediaType.TSV_MEDIATYPE_VALUE } )
	@RequestMapping(value = "/chromosome-report/export/hpp/{chromosome}", method = {RequestMethod.GET})
	public void exportHPPChromosomeEntriesReport(
			@ApiPathParam(name = "chromosome", description = "The chromosome number or name (X,Y..)",  allowedvalues = { "Y"})
			@PathVariable("chromosome")  String chromosome, HttpServletRequest request, HttpServletResponse response) {

		NextprotMediaType mediaType = NextprotMediaType.valueOf(request);

		try (OutputStream os = response.getOutputStream()) {

			String filename = "HPP_chromosome_" + chromosome + "." + mediaType.getExtension();
			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
			chromosomeReportExportService.exportHPPChromosomeEntryReport(chromosome, NextprotMediaType.valueOf(request), os);
		}
		catch (IOException e) {
			throw new NextProtException(e.getMessage()+": cannot export HPP chromosome "+chromosome+" as "+ mediaType);
		}
	}
}
