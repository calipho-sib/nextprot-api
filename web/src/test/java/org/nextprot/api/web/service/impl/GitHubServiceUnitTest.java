package org.nextprot.api.web.service.impl;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.WebUnitBaseTest;
import org.nextprot.api.web.domain.NextProtNews;

/**
 * Testing the github service
 * @author dteixeira
 */
public class GitHubServiceUnitTest extends WebUnitBaseTest {

    @Test
    public void shouldGetANextProtNews() throws Exception {
    	NextProtNews n = GitHubServiceImpl.parseGitHubNewsFilePath("2014/08/25/Google OAuth Support.md");
    	
    	assertEquals(n.getUrl(), "google-oauth-support");
    	assertEquals(n.getTitle(), "Google OAuth Support");

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); 
		Date date = df.parse("2014-08-25");

		assertEquals(n.getPublicationDate(), date);
		assertEquals(n.getPublicationDateFormatted(), "Aug 25, 2014");

    }
    
    
    @Test
    public void shouldReturnNullIfThereIsLessThan2Slashes() throws Exception {
    	NextProtNews n = GitHubServiceImpl.parseGitHubNewsFilePath("2014/08/Google OAuth Support.md");
    	assertTrue(n == null);
    }
    
    @Test
    public void shouldReturnNullIfDateIsNotInCorrectFormat() throws Exception {
    	NextProtNews n = GitHubServiceImpl.parseGitHubNewsFilePath("2014/August/25/Google OAuth Support.md");
    	assertTrue(n == null);
    }


}
