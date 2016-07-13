package org.nextprot.api.web.seo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import java.util.Collections;
import java.util.Map;

import org.hamcrest.core.StringStartsWith;
import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

public class SitemapXMLIntegrationTest extends WebIntegrationBaseTest {

	final Map<String, String> NS = Collections.singletonMap("ns", "http://www.sitemaps.org/schemas/sitemap/0.9");

	@Test
	public void shouldReturnCorrectElementsForSitemap() throws Exception {

		ResultActions actions = this.mockMvc.perform(get("/sitemap"));
		actions.andExpect(xpath( "/ns:urlset",NS).exists());
		actions.andExpect(xpath( "/ns:urlset/ns:url",NS).exists());
		actions.andExpect(xpath( "/ns:urlset/ns:url/ns:loc",NS).exists());
		actions.andExpect(xpath( "/ns:urlset/ns:url/ns:changefreq",NS).exists());
		actions.andExpect(xpath( "/ns:urlset/ns:url/ns:lastmod",NS).exists());
		actions.andExpect(xpath( "/ns:urlset/ns:url/ns:priority",NS).exists());

	}

	@Test
	public void shouldReturnCorrectValuesForSitemap() throws Exception {

		ResultActions actions = this.mockMvc.perform(get("/sitemap"));
		actions.andExpect(xpath("/ns:urlset/ns:url/ns:changefreq",NS).string("weekly"));
		actions.andExpect(xpath("/ns:urlset/ns:url/ns:loc",NS).string(new StringStartsWith("https://search.nextprot.org")));

	}
}
