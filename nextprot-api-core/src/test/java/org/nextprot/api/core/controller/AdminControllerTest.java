package org.nextprot.api.core.controller;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.dbunit.MVCBaseIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

/**
 * The admin controller is only on pro environment
 * @author dteixeira
 */
@ActiveProfiles("pro-test")
public class AdminControllerTest extends MVCBaseIntegrationTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = webAppContextSetup(this.wac).build();
	}

	
	//TODO: remove @ignore
	@Ignore
	@Test
	public void shouldAskForCredentials() throws Exception {

		try {
			this.mockMvc.perform(get("/admin/cache/clear").accept(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());

		} catch (Exception e) {
			assertEquals(e.getCause().getClass(), AuthenticationCredentialsNotFoundException.class);
		}
	}

}
