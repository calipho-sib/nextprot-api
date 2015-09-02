package org.nextprot.api.web.controller;

import java.util.List;

import org.kohsuke.github.GHTree;
import org.nextprot.api.web.domain.NextProtNews;
import org.nextprot.api.web.service.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class GitHubApiController {

	@Autowired
	private GitHubService githubService;

	@ResponseBody
	@RequestMapping(value = "/git/trees/master", method = { RequestMethod.GET })
	public GHTree accessFirstPage() {
		return githubService.getTree();
	}

	@ResponseBody
	@RequestMapping(value = "/contents/{folder}/{page}", method = { RequestMethod.GET })
	public String accessPage(@PathVariable("folder") String folder, @PathVariable("page") String page) {
		return githubService.getPage(folder, page);
	}

	@ResponseBody
	@RequestMapping(value = "/git/news", method = { RequestMethod.GET })
	public List<NextProtNews> accessGitNews() {
		return githubService.getNews();
	}
	

}
