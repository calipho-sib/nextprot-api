package org.nextprot.api.web.seo;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.nextprot.api.web.seo.domain.SeoTags;
import org.nextprot.api.web.seo.service.SeoTagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

/**
 * Testing the seo service
 * 
 * @author pmichel
 */

@ActiveProfiles({ "dev","cache" })
public class SeoServiceIntegrationTest extends WebIntegrationBaseTest {

	@Autowired
	private SeoTagsService seoTagsService = null;

	
	@Test
	public void testSomeHardCodedTags() throws Exception {
		SeoTags tags;
		tags = seoTagsService.getGitHubSeoTags("/about/nextprot");
		Assert.assertTrue(tags!=null);
	}
	
	
	@Test
	public void testSomeDefaultSeoTags() throws Exception {
		SeoTags tags;
		
		tags = seoTagsService.getDefaultSeoTags("/toto");
		Assert.assertTrue(tags!=null);
		Assert.assertEquals("Toto", tags.getTitle());

		tags = seoTagsService.getDefaultSeoTags("/toto/");
		Assert.assertTrue(tags!=null);
		Assert.assertEquals("Toto", tags.getTitle());
		
		tags = seoTagsService.getDefaultSeoTags("/toto/tutu");
		Assert.assertTrue(tags!=null);
		Assert.assertEquals("Toto - Tutu", tags.getTitle());
		
		tags = seoTagsService.getDefaultSeoTags("/toto/tutu?titi=content");
		Assert.assertTrue(tags!=null);
		Assert.assertEquals("Toto - Tutu", tags.getTitle());
		
	}

	
	@Test
	public void testSeoTagsForNews() throws Exception {
		SeoTags tags;
		
		// get default news (most recent one)
		tags = seoTagsService.getNewsSeoTags("/news");
		Assert.assertTrue(tags!=null);
		
		// get default news (most recent one)
		tags = seoTagsService.getNewsSeoTags("/news/");
		Assert.assertTrue(tags!=null);

		// get old specific news
		tags = seoTagsService.getNewsSeoTags("/news/the-nextprot-hupo2014-release");
		Assert.assertTrue(tags!=null);
		Assert.assertEquals("News - The neXtProt HUPO2014 release", tags.getTitle());
	}

	@Test
	public void testSeoTagsForEntry() throws Exception {
		SeoTags tags;
		
		tags = seoTagsService.getEntrySeoTags("/entry/NX_P01308");
		Assert.assertTrue(tags!=null);
		Assert.assertEquals("INS - Insulin - Function", tags.getTitle());
		
		tags = seoTagsService.getEntrySeoTags("/entry/NX_P01308/");
		Assert.assertTrue(tags!=null);
		Assert.assertEquals("INS - Insulin - Function", tags.getTitle());
		
		tags = seoTagsService.getEntrySeoTags("/entry/NX_P01308/function");
		Assert.assertTrue(tags!=null);
		Assert.assertEquals("INS - Insulin - Function", tags.getTitle());
		
		tags = seoTagsService.getEntrySeoTags("/entry/NX_P01308/medical");
		Assert.assertTrue(tags!=null);
		Assert.assertEquals("INS - Insulin - Medical", tags.getTitle());
		
		tags = seoTagsService.getEntrySeoTags("/entry/NX_P01308/expression");
		Assert.assertTrue(tags!=null);
		Assert.assertEquals("INS - Insulin - Expression", tags.getTitle());

		//...
	}
	
	@Test
	public void testSeoTagsForPubli() throws Exception {
		SeoTags tags;
		tags = seoTagsService.getPublicationSeoTags("/publication/43778635");
		Assert.assertTrue(tags!=null);
		String title = "Childhood adversity moderates the effect of ADH1B on risk for alcohol-related phenotypes in Jewish Israeli drinkers.";
		title += " - Proteins"; 
		Assert.assertEquals(title, tags.getTitle());
		
		// just check that we have same tag with this url
		tags = seoTagsService.getPublicationSeoTags("/publication/43778635/");
		Assert.assertTrue(tags!=null);
		Assert.assertEquals(title, tags.getTitle());
		
		// just check that we have same tag with this url
		tags = seoTagsService.getPublicationSeoTags("/publication/43778635/proteins");
		Assert.assertTrue(tags!=null);
		Assert.assertEquals(title, tags.getTitle());
		
	}
	
	@Test
	public void testSeoTagsForTerm() throws Exception {

		String baseTitle = "GO:0042802 - Identical protein binding - ";
		SeoTags tags;
		
		tags = seoTagsService.getTermSeoTags("/term/GO:0042802");
		Assert.assertTrue(tags!=null);
		Assert.assertEquals(baseTitle + "Proteins", tags.getTitle());
		
		tags = seoTagsService.getTermSeoTags("/term/GO:0042802/");
		Assert.assertTrue(tags!=null);
		Assert.assertEquals(baseTitle + "Proteins", tags.getTitle());
		
		tags = seoTagsService.getTermSeoTags("/term/GO:0042802/proteins");
		Assert.assertTrue(tags!=null);
		Assert.assertEquals(baseTitle + "Proteins", tags.getTitle());
		

		tags = seoTagsService.getTermSeoTags("/term/GO:0042802/ancestors");
		Assert.assertTrue(tags!=null);
		Assert.assertEquals(baseTitle + "Ancestors", tags.getTitle());
		
		tags = seoTagsService.getTermSeoTags("/term/GO:0042802/tree");
		Assert.assertTrue(tags!=null);
		Assert.assertEquals(baseTitle + "Tree", tags.getTitle());
		
		
	}

	
	/*
	System.out.println("title:"+ tags.getTitle());
	System.out.println("descr:"+ tags.getMetaDescription());
	System.out.println("h1:"+ tags.getH1());
*/
	
	
}
