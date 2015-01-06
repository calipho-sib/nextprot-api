package org.nextprot.api.web;

import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.MVCBaseSecurityIntegrationTest;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserApplicationControllerSecurityTest extends MVCBaseSecurityIntegrationTest {

	private String content = "{\"name\":\"test\"}";

	@Test
	public void shouldReturn401ForAbsentToken() throws Exception {

		this.mockMvc.perform(post("/user/dani/applications").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(content)).andExpect(
				status().isUnauthorized());
	}

	@Test
	public void shouldReturn401ForAnInvalidToken() throws Exception {

		this.mockMvc.perform(
				post("/user/dani/applications").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer a.b.c").accept(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void shouldReturn403ForInvalidPrivilege() throws Exception {

		this.mockMvc.perform(
				post("/user/dani/applications").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer a.b.c").accept(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isForbidden());
	}

	@Test
	public void showReturn200ForAValidToken() throws Exception {

		String token = generateTokenWithExpirationDate(1, TimeUnit.DAYS, Arrays.asList(new String[]{"ROLE_USER"}));
		
		this.mockMvc.perform(
				post("/user/dani/applications.json").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + token).accept(MediaType.APPLICATION_JSON)
						.content(content)).andExpect(status().isOk());
	}
}
