package org.nextprot.api.core.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.core.domain.EntryReport;
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

	@ApiMethod(path = "/chromosomes/{chromosome}", verb = ApiVerb.GET, description = "Get informations from all entries found on a given chromosome", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/chromosomes/{chromosome}", method = { RequestMethod.GET })
	@ResponseBody
	public List<EntryReport> exportChromosomeEntryReport(
			@ApiPathParam(name = "chromosome", description = "The chromosome number or name (X,Y..)",  allowedvalues = { "Y"}) @PathVariable("chromosome")  String chromosome) {

		return chromosomeReportService.exportChromosomeEntryReport(chromosome);
	}
}
