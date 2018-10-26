package org.nextprot.api.web.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.core.service.ReleaseInfoService;
import org.nextprot.api.core.service.StatisticsService;
import org.nextprot.api.web.NXVelocityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.EnumSet;
import java.util.Map;

@Controller
@Api(name = "Release Info", description = "Method to retrieve information about the current release")
public class ReleaseInfoController {

	@Autowired
	private ReleaseInfoService releaseService;
    @Autowired
    private StatisticsService statisticsService;

    @ApiMethod(path = "/release-info", verb = ApiVerb.GET, description = "Gets information about the current neXtProt release", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	@RequestMapping(value = "/release-info", method = { RequestMethod.GET })
	public String releaseInformation(Model model) {
		model.addAttribute(NXVelocityContext.RELEASE_NUMBER, releaseService.findReleaseVersions());
		return "release-info";
	}

	@ApiMethod(path = "/release-stats", verb = ApiVerb.GET, description = "Gets data statistics about the current neXtProt release", produces = {MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping(value = "/release-stats", method = { RequestMethod.GET })
	public String releaseStats(Model model) {
		model.addAttribute(NXVelocityContext.RELEASE_STATS, releaseService.findReleaseStats());
		return "release-stats";
	}

	@ApiMethod(path = "/release-data-sources", verb = ApiVerb.GET, description = "Gets data sources of current neXtProt release", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	@RequestMapping(value = "/release-data-sources", method = { RequestMethod.GET })
	public String releaseDatasources(Model model) {
		model.addAttribute(NXVelocityContext.RELEASE_DATA_SOURCES, releaseService.findReleaseDatasources());
		return "release-data-sources";
	}

	@RequestMapping(value = "/release-contents", method = { RequestMethod.GET })
	public String releaseContents(Model model) {
		model.addAttribute(NXVelocityContext.RELEASE_NUMBER, releaseService.findReleaseVersions());
		model.addAttribute(NXVelocityContext.RELEASE_DATA_SOURCES, releaseService.findReleaseDatasources());
		return "release-contents";
	}

    @RequestMapping(value = "/placeholder-stats", method = { RequestMethod.GET })
    @ResponseBody
    public Map<StatisticsService.Counter, Integer> statsByPlaceholder() {
        return statisticsService.getStatsByPlaceholder(EnumSet.allOf(StatisticsService.Counter.class));
    }
}
