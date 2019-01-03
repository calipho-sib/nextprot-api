package org.nextprot.api.web.service.impl;

import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.nextprot.api.web.domain.NextProtNews;
import org.nextprot.api.web.service.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Testing the github service
 * 
 * @author dteixeira
 */
public class GitHubServiceIntegrationTest extends WebIntegrationBaseTest {

	@Autowired
	private GitHubService ghService = null;

	@Test
	public void testGitHubService() throws Exception {
		List<NextProtNews> ns = ghService.getNews();
		assertTrue(ns.size() > 1);
	}

	@Test
	public void testGitHubSpecial() throws Exception {
		String content = ghService.getPage("json-config", "seotags");
		assertTrue(!content.isEmpty());
	}

	@Test
	public void testGitHubNews() throws Exception {
		String content = ghService.getPage("news", "september-2015-nextprot-release");
		assertTrue(!content.isEmpty());
	}

	@Test
	public void testGitHubHelp() throws Exception {
		String content = ghService.getPage("help", "faq");
		assertTrue(!content.isEmpty());
	}

	@Test
	public void testGitHubPage() throws Exception {
		String content = ghService.getPage("pages", "what-is-new");
		assertTrue(!content.isEmpty());
	}
}
