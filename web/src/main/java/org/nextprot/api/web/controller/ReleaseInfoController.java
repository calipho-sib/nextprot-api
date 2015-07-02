package org.nextprot.api.web.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.service.ReleaseInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@Api(name = "Release Info", description = "Method to retrieve information about the current release")
public class ReleaseInfoController {

	@Autowired
	private ReleaseInfoService releaseService;

    @ApiMethod(path = "/release-contents", verb = ApiVerb.GET, description = "Gets information about the current neXtProt release", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	@RequestMapping(value = "/release-contents", method = { RequestMethod.GET })
	public String releaseInformation(Model model) {
		model.addAttribute("release", releaseService.findReleaseContents());
		return "release-contents";
	}

    @ApiMethod(path = "/release-statistics", verb = ApiVerb.GET, description = "Gets information about the current neXtProt statistics", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	@RequestMapping(value = "/release-statistics", method = { RequestMethod.GET })
	public String releaseInformation() {
    	//TODO take info from the database (stats view table)
    	throw new NextProtException("Not implemented yet");
	}

}
