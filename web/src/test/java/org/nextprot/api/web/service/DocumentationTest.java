package org.nextprot.api.web.service;

import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.dbunit.CommonsUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Gets documentation from GitHub
 * 
 * @author dteixeira
 */
@Ignore
public class DocumentationTest extends CommonsUnitBaseTest {

	@Autowired
	private GitHubService service;

	@Test
	public void shoudGetDocumentationFromGitHub() throws Exception {
		String s = service.getPage("test");
		assertTrue(s.contains("USED-FOR-TEST-DO-NOT-DELETE"));
	}

	@Test
	public void shoudGetDirectoryContentGitHub() throws Exception {
		assertTrue(service.getTree().getTree().length > 0);
	}

}
