package org.nextprot.api.web.controller.genomic.misctobeorganized;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.MVCBaseIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

public class PublicationControllerTest extends MVCBaseIntegrationTest {

	@Autowired private WebApplicationContext wacAppConfiguration;
	private MockMvc mockMvc;
	
	@Before
	public void setup() {
		this.mockMvc = webAppContextSetup(this.wacAppConfiguration).build();
	}
	
	@Test
	@Ignore
	public void getPublication() throws Exception {
		this.mockMvc.perform(get("/publication/7126674.xml"))
			.andExpect(status().isOk())
			.andExpect(xpath("/publication").exists())
			.andExpect(xpath("/publication//@type").string("ARTICLE"))
			.andExpect(xpath("/publication//@id").string("7126674"))
			.andExpect(xpath("/publication/publicationDate").string("2010-01-01"))
			.andExpect(xpath("/publication/authors").exists())
			.andExpect(xpath("/publication/xrefs").exists());
	}
}
