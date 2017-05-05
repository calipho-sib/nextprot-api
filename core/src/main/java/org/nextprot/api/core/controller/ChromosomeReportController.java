package org.nextprot.api.core.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.core.domain.ChromosomeReport;
import org.nextprot.api.core.service.ChromosomeReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@Api(name = "Chromosome reports", description = "Reports statistics about entries on chromosome")
public class ChromosomeReportController {

	@Autowired
	private ChromosomeReportService chromosomeReportService;

	@ApiMethod(path = "/chromosome-names", verb = ApiVerb.GET, description = "Get the list of chromosomes referenced in neXtProt",
			produces = { MediaType.APPLICATION_JSON_VALUE } )
	@RequestMapping(value = "/chromosome-names", method = {RequestMethod.GET})
	@ResponseBody
	public List<String> getChromosomes() {

		return chromosomeReportService.getChromosomes();
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

	@ApiMethod(path = "/chromosome-report/{chromosome}/summary", verb = ApiVerb.GET, description = "Export summary of neXtProt entries coming from genes located on a given chromosome",
			produces = { MediaType.APPLICATION_JSON_VALUE } )
	@RequestMapping(value = "/chromosome-report/{chromosome}/summary", method = {RequestMethod.GET})
	@ResponseBody
	public ChromosomeReport.Summary reportChromosomeSummary(
			@ApiPathParam(name = "chromosome", description = "The chromosome number or name (X,Y..)",  allowedvalues = { "Y"})
			@PathVariable("chromosome")  String chromosome) {

		return chromosomeReportService.reportChromosome(chromosome).getSummary();
	}
}
