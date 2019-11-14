package org.nextprot.api.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.release.ReleaseInfoStats;
import org.nextprot.api.core.service.ReleaseInfoService;
import org.nextprot.api.core.service.StatisticsService;
import org.nextprot.api.web.NXVelocityContext;
import org.nextprot.api.web.service.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Map;

@Controller
@Api(name = "Release Info", description = "Method to retrieve information about the current release")
public class ReleaseInfoController {

	@Autowired
	private ReleaseInfoService releaseService;
    @Autowired
    private StatisticsService statisticsService;
    @Autowired
    private GitHubService githubService;

    @ApiMethod(path = "/release-info", verb = ApiVerb.GET, description = "Gets information about the current neXtProt release", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	@RequestMapping(value = "/release-info", method = { RequestMethod.GET })
	public String releaseInformation(Model model) {
		model.addAttribute(NXVelocityContext.RELEASE_NUMBER, releaseService.findReleaseVersions());
		return "release-info";
	}

	@ApiMethod(path = "/release-stats", verb = ApiVerb.GET, description = "Gets data statistics about the current neXtProt release", produces = {MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping(value = "/release-stats", method = { RequestMethod.GET })
	public String releaseStats(Model model) {
        ReleaseInfoStats rs = releaseService.findReleaseStats();
        rs.setDatabaseReleaseList(githubService.getReleaseStatList());
        model.addAttribute(NXVelocityContext.RELEASE_STATS, rs);
		return "release-stats";
	}

    @ApiMethod(path = "/release-stats/{databaseRelease}", verb = ApiVerb.GET, description = "Gets data statistics about the current neXtProt release", produces = {MediaType.APPLICATION_JSON_VALUE})
    @RequestMapping(value = "/release-stats/{databaseRelease}", method = { RequestMethod.GET })
    public String releaseStatsByDbRelease(
            @ApiPathParam(name = "databaseRelease", description = "The database release of the neXtProt release. For example, '2018-01-17'",  allowedvalues = { "2018-01-17"})
            @PathVariable("databaseRelease") String databaseRelease,
            Model model) {

        String page;
        try {
            page = githubService.getPage("release-stats", databaseRelease);
        } catch (NextProtException e) {
            throw new NextProtException(databaseRelease+": invalid database release");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ReleaseInfoStats ris;
        try {
            ris = objectMapper.readValue(objectMapper.readTree(page).get("releaseStats").toString(), ReleaseInfoStats.class);
        } catch (IOException e) {
            throw new NextProtException("Cannot read json file for database release " + databaseRelease + ":" + e.getLocalizedMessage());
        }
        ris.setDatabaseReleaseList(githubService.getReleaseStatList());
        model.addAttribute(NXVelocityContext.RELEASE_STATS, ris);
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
