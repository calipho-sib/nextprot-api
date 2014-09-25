package org.nextprot.api.web.error;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.dbunit.MVCBaseIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

public class ErrorHandlingWebTest extends MVCBaseIntegrationTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = webAppContextSetup(this.wac).build();
	}

	@Test
	@Ignore //TODO THIS TEST SHOULD BE FIXED!!!!!!!!!!!!!!
	public void shouldGet404() throws Exception {
		this.mockMvc.perform(get("/gsjkabgékajsb/NX_P0138/identifiers.xml").accept(MediaType.APPLICATION_XML)).andExpect(status().isNotFound());
	}

	@Test 
	@Ignore //TODO THIS TEST SHOULD BE FIXED!!!!!!!!!!!!!!
	public void shouldGet200Error() throws Exception {
		this.mockMvc.perform(get("/entry/fasjbfkbafskajs.xml").accept(MediaType.APPLICATION_XML))
		.andExpect(status().isOk())
        .andExpect(xpath("/error").exists());
	}

}
