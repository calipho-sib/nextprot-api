package org.nextprot.api.web.jsondoc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import org.hamcrest.core.StringContains;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.MVCBaseSecurityIntegrationTest;
import org.springframework.test.web.servlet.ResultActions;

/**
 * @author dteixeira
 */
//TODO add mock in here!!!
@Ignore
public class XMLWebTest extends MVCBaseSecurityIntegrationTest {

	@Test
	public void shouldGetXml() throws Exception {
		ResultActions result = this.mockMvc.perform(get("/entry/NX_P01308.xml"));
		result.andExpect(status().isOk());
		result.andExpect(content().contentType("application/xml;charset=UTF-8"));

	}
	
	@Test 
	public void shouldGetXmlWellFormatted() throws Exception {

		ResultActions result = this.mockMvc.perform(get("/entry/NX_P01308.xml"));

		result.andExpect(xpath("//publication[@id='28338750']").exists());
		// β-sheets
		result.andExpect(content().string(new StringContains("β-sheets")));
	}

	@Test
	public void shouldGetXmlWellFormatted2() throws Exception {

		ResultActions result = this.mockMvc.perform(get("/entry/NX_P48730.xml"));

		result.andExpect(xpath("//publication[@id='14815724']//journal").exists());
		result.andExpect(content().string(new StringContains("&amp; cell biology")));

	}

}
