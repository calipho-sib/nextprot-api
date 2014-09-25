package org.nextprot.api.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.hamcrest.core.StringContains;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.commons.dbunit.MVCBaseIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

/**
 * Class used for testing Genomic Mapping controller
 * 
 * @author dteixeira
 */
public class CharEncodingTest extends MVCBaseIntegrationTest{

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = webAppContextSetup(this.wac).build();
	}

	@Test
	public void shouldGetEscapedCharsInXML() throws Exception {
		this.mockMvc.perform(get("/entry/NX_P48730.xml")).
		andExpect(MockMvcResultMatchers.content().contentType("application/xml;charset=UTF-8")).
		andExpect(MockMvcResultMatchers.content().string(new StringContains("biochemistry &amp; cell biology"))). // biochemistry &amp; cell biology
		andExpect(MockMvcResultMatchers.content().string(new StringContains("B&#252;rckst&#252;mmer"))); // The same as Tilmann Bürckstümmer
		//andExpect(MockMvcResultMatchers.header().encoding("UTF-8")) //TODO UTF 8 does not show here, seems a bug of spring mock mvc because in the real browser it appears
		;
	}
	
	
	@Test
	public void shouldGetEscapedCharsInJson() throws Exception {
		this.mockMvc.perform(get("/entry/NX_P48730.json")).
		andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)). //application/xml;charset=UTF-8
		andExpect(MockMvcResultMatchers.content().string(new StringContains("biochemistry & cell biology"))).
		andExpect(MockMvcResultMatchers.content().string(new StringContains("Bürckstümmer"))); 
		//andExpect(MockMvcResultMatchers.header().encoding("UTF-8")) //TODO UTF 8 does not show here, seems a bug of spring mock mvc because in the real browser it appears
		;
	}

}
