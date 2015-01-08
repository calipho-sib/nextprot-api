package org.nextprot.api.web;

import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.MVCBaseSecurityTest;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminControllerSecurityTest extends MVCBaseSecurityTest {

	private String content = "{\"name\":\"test\"}";

	private String url = "/admin/cache/clear";

	@Test
	public void shouldReturn401ForAbsentToken() throws Exception {

		this.mockMvc.perform(
				get(url).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(content)).andExpect(
				status().isUnauthorized());
	}

	@Test
	public void shouldReturn401ForAnInvalidToken() throws Exception {

		this.mockMvc.perform(
				get(url).contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer a.b.c").accept(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void shouldReturn200ForAValidToken() throws Exception {

		String token = generateTokenWithExpirationDate(1, TimeUnit.DAYS, Arrays.asList(new String[]{"ROLE_ADMIN"}));
		
		this.mockMvc.perform(
				get(url).contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + token).accept(MediaType.APPLICATION_JSON)
						.content(content)).andExpect(status().isOk());
	}

	@Test
	public void shouldReturn401ForInsufficienPrivilege() throws Exception {

		String token = generateTokenWithExpirationDate(1, TimeUnit.DAYS, Arrays.asList(new String[]{"ROLE_USER"}));

		this.mockMvc.perform(
				get(url).contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + token).accept(MediaType.APPLICATION_JSON)
						.content(content)).andExpect(status().isUnauthorized());
	}
}
