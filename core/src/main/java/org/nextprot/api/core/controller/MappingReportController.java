package org.nextprot.api.core.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.service.MappingReportService;
import org.nextprot.api.core.service.export.format.NextprotMediaType;
import org.nextprot.api.core.service.export.io.MappingReportWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@Api(name = "Mapping reports", description = "Reports various mappings to entries")
public class MappingReportController {

	@Autowired private 
	MappingReportService mappingReportService;


	@ApiMethod(path = "/mapping/hpa", verb = ApiVerb.GET, description = "Export mapping of HPA antibodies to neXtProt entries",
			produces = { NextprotMediaType.SPLOG_MEDIATYPE_VALUE, NextprotMediaType.TSV_MEDIATYPE_VALUE } )
	@RequestMapping(value = "/mapping/nextprot_hpa", method = {RequestMethod.GET})
	public void exportHPAMappingReportFile(HttpServletRequest request, HttpServletResponse response) {

		NextprotMediaType mediaType = NextprotMediaType.valueOf(request);

		try (OutputStream os = response.getOutputStream()) {
			String filename = "nextprot_hpa." + mediaType.getExtension();
			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
			List<String> data = mappingReportService.findHPAMapping();
			MappingReportWriter writer = new MappingReportWriter(os);
			writer.writeHPAMapping(data, mediaType);
			writer.close();
		}
		catch (IOException e) {
			throw new NextProtException(e.getMessage()+": cannot export HPA antibodies mapping as "+ mediaType);
		}
	}
	
}
