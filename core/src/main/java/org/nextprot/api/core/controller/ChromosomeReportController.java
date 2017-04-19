package org.nextprot.api.core.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.core.service.ChromosomeReportService;
import org.nextprot.api.core.service.export.format.FileFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@Api(name = "Chromosome reports", description = "Reports statistics about entries on chromosome")
public class ChromosomeReportController {

	@Autowired
	private ChromosomeReportService chromosomeReportService;

	@ApiMethod(path = "/export/chromosome/{chromosome}", verb = ApiVerb.GET, description = "Export informations of neXtProt entries coming from genes located on a given chromosome",
			produces = { MediaType.APPLICATION_JSON_VALUE, "text/tab-separated-values" } )
	@RequestMapping(value = "/export/chromosome/{chromosome}", method = {RequestMethod.GET})
	@ResponseBody
	public void exportChromosomeEntries(
			@ApiPathParam(name = "chromosome", description = "The chromosome number or name (X,Y..)",  allowedvalues = { "Y"})
			@PathVariable("chromosome")  String chromosome, HttpServletRequest request, HttpServletResponse response) {

		chromosomeReportService.exportChromosomeEntryReport(chromosome, FileFormat.valueOf(request), response);
	}
}
