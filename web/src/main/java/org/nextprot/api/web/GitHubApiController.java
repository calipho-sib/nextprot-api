package org.nextprot.api.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nextprot.api.web.service.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class GitHubApiController {

	@Autowired
	private GitHubService githubService;

	@ResponseBody
	@RequestMapping(value = "/pages/git/trees/master", method = { RequestMethod.GET })
	public String accessFirstPage(@RequestBody String body, HttpServletRequest request, HttpServletResponse response) {

		return "{\"sha\":\"a1890b331210cbc6758d5c6c37b610a7d51a2c61\",\"url\":\"https://api.github.com/repos/calipho-sib/nextprot-docs/git/trees/a1890b331210cbc6758d5c6c37b610a7d51a2c61\",\"tree\":[{\"path\":\"README.md\",\"mode\":\"100644\",\"type\":\"blob\",\"sha\":\"530d686339c76a913a9585c12462eac7c2981c0e\",\"size\":78,\"url\":\"https://api.github.com/repos/calipho-sib/nextprot-docs/git/blobs/530d686339c76a913a9585c12462eac7c2981c0e\"},{\"path\":\"blog\",\"mode\":\"040000\",\"type\":\"tree\",\"sha\":\"89c02d39b235b08ff3e85144fb86f3896f712280\",\"url\":\"https://api.github.com/repos/calipho-sib/nextprot-docs/git/trees/89c02d39b235b08ff3e85144fb86f3896f712280\"},{\"path\":\"blog/2014\",\"mode\":\"040000\",\"type\":\"tree\",\"sha\":\"b8b3aa7a384094621709a915ec07d888c373d1df\",\"url\":\"https://api.github.com/repos/calipho-sib/nextprot-docs/git/trees/b8b3aa7a384094621709a915ec07d888c373d1df\"},{\"path\":\"blog/2014/11\",\"mode\":\"040000\",\"type\":\"tree\",\"sha\":\"7097434d18a1167b1566799502c52ece302e6f05\",\"url\":\"https://api.github.com/repos/calipho-sib/nextprot-docs/git/trees/7097434d18a1167b1566799502c52ece302e6f05\"},{\"path\":\"blog/2014/11/05\",\"mode\":\"040000\",\"type\":\"tree\",\"sha\":\"7269e7f787c4112e9a59e5ac173395e130bf7964\",\"url\":\"https://api.github.com/repos/calipho-sib/nextprot-docs/git/trees/7269e7f787c4112e9a59e5ac173395e130bf7964\"},{\"path\":\"blog/2014/11/05/Google OAuth Support.md\",\"mode\":\"100644\",\"type\":\"blob\",\"sha\":\"5f6a409fdad5189fc651c1730223b261ed115544\",\"size\":6352,\"url\":\"https://api.github.com/repos/calipho-sib/nextprot-docs/git/blobs/5f6a409fdad5189fc651c1730223b261ed115544\"},{\"path\":\"docs\",\"mode\":\"040000\",\"type\":\"tree\",\"sha\":\"d07a50bb1d71436dc2bf266a59e54ba1fdae4c30\",\"url\":\"https://api.github.com/repos/calipho-sib/nextprot-docs/git/trees/d07a50bb1d71436dc2bf266a59e54ba1fdae4c30\"},{\"path\":\"docs/01_Introduction.md\",\"mode\":\"100644\",\"type\":\"blob\",\"sha\":\"4855384b99cb312d8b2a51daac9a8036abcefa69\",\"size\":620,\"url\":\"https://api.github.com/repos/calipho-sib/nextprot-docs/git/blobs/4855384b99cb312d8b2a51daac9a8036abcefa69\"},{\"path\":\"docs/02_Getting Started.md\",\"mode\":\"100644\",\"type\":\"blob\",\"sha\":\"273e8bb0761ce57ca9a136b316e82a53c095a29a\",\"size\":93,\"url\":\"https://api.github.com/repos/calipho-sib/nextprot-docs/git/blobs/273e8bb0761ce57ca9a136b316e82a53c095a29a\"},{\"path\":\"pages\",\"mode\":\"040000\",\"type\":\"tree\",\"sha\":\"ae702cfe95895dfd272eb4ae868936d83d77e1f5\",\"url\":\"https://api.github.com/repos/calipho-sib/nextprot-docs/git/trees/ae702cfe95895dfd272eb4ae868936d83d77e1f5\"},{\"path\":\"pages/about.md\",\"mode\":\"100644\",\"type\":\"blob\",\"sha\":\"413b20857bc96c380df6ba79067225af17b2e46b\",\"size\":6819,\"url\":\"https://api.github.com/repos/calipho-sib/nextprot-docs/git/blobs/413b20857bc96c380df6ba79067225af17b2e46b\"},{\"path\":\"pages/copyright.md\",\"mode\":\"100644\",\"type\":\"blob\",\"sha\":\"3af027164d830bd71d5615df67a2f998a6f1d6b1\",\"size\":518,\"url\":\"https://api.github.com/repos/calipho-sib/nextprot-docs/git/blobs/3af027164d830bd71d5615df67a2f998a6f1d6b1\"},{\"path\":\"pages/demo-sparql-queries.md\",\"mode\":\"100644\",\"type\":\"blob\",\"sha\":\"fbe2da5ec07d8c51b4d9119aa2d1fdc297f2d215\",\"size\":27594,\"url\":\"https://api.github.com/repos/calipho-sib/nextprot-docs/git/blobs/fbe2da5ec07d8c51b4d9119aa2d1fdc297f2d215\"},{\"path\":\"pages/faq.md\",\"mode\":\"100644\",\"type\":\"blob\",\"sha\":\"25f0044ae77a383b19da5a3a8403c383b6cd17e8\",\"size\":29,\"url\":\"https://api.github.com/repos/calipho-sib/nextprot-docs/git/blobs/25f0044ae77a383b19da5a3a8403c383b6cd17e8\"},{\"path\":\"pages/learn-advanced-search.md\",\"mode\":\"100644\",\"type\":\"blob\",\"sha\":\"23e774f0c3c30101fe71ec5fd48c31cb23412f8f\",\"size\":2148,\"url\":\"https://api.github.com/repos/calipho-sib/nextprot-docs/git/blobs/23e774f0c3c30101fe71ec5fd48c31cb23412f8f\"},{\"path\":\"pages/learn-developer-api.md\",\"mode\":\"100644\",\"type\":\"blob\",\"sha\":\"33495f24195822d9f88d4912c4dc3055478a0ed7\",\"size\":15,\"url\":\"https://api.github.com/repos/calipho-sib/nextprot-docs/git/blobs/33495f24195822d9f88d4912c4dc3055478a0ed7\"},{\"path\":\"pages/learn-protein-lists.md\",\"mode\":\"100644\",\"type\":\"blob\",\"sha\":\"6f38f3180e0141622da93fb4c985ea26c82f2890\",\"size\":2097,\"url\":\"https://api.github.com/repos/calipho-sib/nextprot-docs/git/blobs/6f38f3180e0141622da93fb4c985ea26c82f2890\"},{\"path\":\"pages/learn-search-results.md\",\"mode\":\"100644\",\"type\":\"blob\",\"sha\":\"29b4a997aaef797ffcbfe2cac9a98629a4fa0931\",\"size\":3001,\"url\":\"https://api.github.com/repos/calipho-sib/nextprot-docs/git/blobs/29b4a997aaef797ffcbfe2cac9a98629a4fa0931\"},{\"path\":\"pages/learn-simple-search.md\",\"mode\":\"100644\",\"type\":\"blob\",\"sha\":\"ce6b098574939a75eaee41780965e1b9793b4ecf\",\"size\":2334,\"url\":\"https://api.github.com/repos/calipho-sib/nextprot-docs/git/blobs/ce6b098574939a75eaee41780965e1b9793b4ecf\"},{\"path\":\"pages/legal disclaimer.md\",\"mode\":\"100644\",\"type\":\"blob\",\"sha\":\"8db9ee5c1ee27f30b4b1e6b0e186c066cdba6206\",\"size\":5737,\"url\":\"https://api.github.com/repos/calipho-sib/nextprot-docs/git/blobs/8db9ee5c1ee27f30b4b1e6b0e186c066cdba6206\"},{\"path\":\"pages/snorql-headlines.md\",\"mode\":\"100644\",\"type\":\"blob\",\"sha\":\"029eb1937309f73889e2a909fd3a3d6b34f6bf48\",\"size\":659,\"url\":\"https://api.github.com/repos/calipho-sib/nextprot-docs/git/blobs/029eb1937309f73889e2a909fd3a3d6b34f6bf48\"},{\"path\":\"pages/what-is-new.md\",\"mode\":\"100644\",\"type\":\"blob\",\"sha\":\"27d631af5c31d1d2b8ff26abc980d3f7d9932c73\",\"size\":474,\"url\":\"https://api.github.com/repos/calipho-sib/nextprot-docs/git/blobs/27d631af5c31d1d2b8ff26abc980d3f7d9932c73\"}],\"truncated\":false}";
	}

	@ResponseBody
	@RequestMapping(value = "pages/contents/pages/{page}", method = { RequestMethod.GET })
	public String accessPage(@PathVariable("page") String page, @RequestBody String body, HttpServletRequest request, HttpServletResponse response) throws IOException {
		return githubService.getPage(page);
	}

}
