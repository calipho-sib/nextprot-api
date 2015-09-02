package org.nextprot.api.web.service.impl;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.nextprot.api.web.domain.NextProtNews;
import org.nextprot.api.web.service.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Testing the github service
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
}
