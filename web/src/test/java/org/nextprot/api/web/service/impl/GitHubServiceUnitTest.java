package org.nextprot.api.web.service.impl;

import static org.junit.Assert.assertTrue;

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
    	NextProtNews n = GitHubServiceImpl.parseGitHubNewsFilePath("2015-08-25 | hupo-conf-2015 | HUPO Conference 2015.md");
    	
    	assertTrue(n.getUrl().equals("hupo-conf-2015"));
    	assertTrue(n.getTitle().equals("HUPO Conference 2015"));

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); 
		Date date = df.parse("2015-08-25");

    	assertTrue(n.getPublicationDate().equals(date));
    }
    
    
    @Test
    public void shouldReturnNullIfThereIsMoreThan3Pipes() throws Exception {
    	NextProtNews n = GitHubServiceImpl.parseGitHubNewsFilePath("2015-08-25 | hupo-conf-2015 | HUPO Conference 2015 | invalid token.md");
    	assertTrue(n == null);
    }
    
    @Test
    public void shouldReturnNullIfDateIsNotInCorrectFormat() throws Exception {
    	NextProtNews n = GitHubServiceImpl.parseGitHubNewsFilePath("2015-August-25 | hupo-conf-2015 | HUPO Conference 2015.md");
    	assertTrue(n == null);
    }


}
