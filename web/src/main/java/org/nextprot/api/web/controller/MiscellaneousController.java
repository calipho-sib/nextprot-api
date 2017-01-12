package org.nextprot.api.web.controller;

import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.service.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

// temporarily hidden from API page until I know how to do it properly
//@Api(name = "Miscellaneous", description = "Useful API methods")
@Controller
public class MiscellaneousController {

	@Autowired private PublicationService publicationService;
	
	@ResponseBody
	@ApiMethod(path = "/publication/{publicationId}", verb = ApiVerb.GET, description = "Gets a publication by internal id", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/publication/{publicationId}", method = { RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE)
	public Publication getPublicationById(
			@ApiPathParam(name = "publicationId", description = "The internal id of the publication.",  allowedvalues = { "47508984"})
			@PathVariable("publicationId") String publicationId) {
		long id;
		try {
			id=Long.parseLong(publicationId);
		} catch (NumberFormatException e) {id=0;};
		return publicationService.findPublicationById(id);
	}
	
	
}
