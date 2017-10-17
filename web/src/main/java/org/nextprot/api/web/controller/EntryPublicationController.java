package org.nextprot.api.web.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.core.domain.publication.EntryPublication;
import org.nextprot.api.core.domain.publication.EntryPublicationReport;
import org.nextprot.api.core.service.EntryPublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Api(name = "Entry Publications", description = "Method to retrieve a publications linked to a neXtProt entry")
public class EntryPublicationController {

	@Autowired private EntryPublicationService entryPublicationService;

	@ApiMethod(path = "/entry-publications/{entry}", verb = ApiVerb.GET, description = "Exports neXtProt entry publications",
			produces = { MediaType.APPLICATION_JSON_VALUE })
	@RequestMapping(value = "/entry-publications/{entry}", method = { RequestMethod.GET })
	@ResponseBody
	public EntryPublicationReport exportPublicationEntry(
			@ApiPathParam(name = "entry", description = "The name of the neXtProt entry. For example, the insulin: NX_P01308",  allowedvalues = { "NX_P01308"})
			@PathVariable("entry") String entryName) {

		return entryPublicationService.reportEntryPublication(entryName);
	}

	@ApiMethod(path = "/entry-publications/{entry}/{id}", verb = ApiVerb.GET, description = "Exports neXtProt entry publication",
			produces = { MediaType.APPLICATION_JSON_VALUE })
	@RequestMapping(value = "/entry-publications/{entry}/{pubid}", method = { RequestMethod.GET })
	@ResponseBody
	public EntryPublication getPublicationEntry(
			@ApiPathParam(name = "entry", description = "The name of the neXtProt entry. For example, the insulin: NX_P01308",  allowedvalues = { "NX_P01308"})
			@PathVariable("entry") String entryName,
			@ApiPathParam(name = "pubid", description = "A publication id")
			@PathVariable("pubid") long publicationId) {

		return entryPublicationService.reportEntryPublication(entryName).getEntryPublication(publicationId);
	}
}
