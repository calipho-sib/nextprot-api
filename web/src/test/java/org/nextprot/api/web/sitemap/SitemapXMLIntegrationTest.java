package org.nextprot.api.web.sitemap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import org.hamcrest.core.StringStartsWith;
import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.springframework.test.web.servlet.ResultActions;

public class SitemapXMLIntegrationTest extends WebIntegrationBaseTest {

	@Test
	public void shouldReturnCorrectElementsForSitemap() throws Exception {

		ResultActions actions = this.mockMvc.perform(get("/sitemap.xml"));
		actions.andExpect(xpath("urls/url/changefreq").exists());
		actions.andExpect(xpath("urls/url/lastmod").exists());
		actions.andExpect(xpath("urls/url/loc").exists());
		actions.andExpect(xpath("urls/url/priority").exists());

	}

	@Test
	public void shouldReturnCorrectValuesForSitemap() throws Exception {

		ResultActions actions = this.mockMvc.perform(get("/sitemap.xml"));
		actions.andExpect(xpath("urls/url/changefreq").string("weekly"));
		actions.andExpect(xpath("urls/url/loc").string(new StringStartsWith("https://search.nextprot.org")));

	}
}
