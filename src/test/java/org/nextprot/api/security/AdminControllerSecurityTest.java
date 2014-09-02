package org.nextprot.api.security;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.nextprot.api.dbunit.MVCBaseIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

public class AdminControllerSecurityTest extends MVCBaseIntegrationTest {

	@Test
	public void shouldAskForCredentials() throws Exception {
		try {
			this.mockMvc.perform(post("/user/dani/applications.json").contentType(MediaType.APPLICATION_JSON).
					accept(MediaType.APPLICATION_JSON).content("{\"name\":\"name\"}")).
					andExpect(status().isForbidden());

		} catch (Exception e) {
			assertEquals(e.getCause().getClass(), AuthenticationCredentialsNotFoundException.class);
		}
	}
}
