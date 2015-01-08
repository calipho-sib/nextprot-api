package org.nextprot.api.web;

import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.MVCBaseSecurityTest;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserApplicationControllerSecurityTest extends MVCBaseSecurityTest {

	private String url = "/queries/public";

	@Test
	public void shouldGetReturn200ForAValidToken() throws Exception {

		String token = generateTokenWithExpirationDate(1, TimeUnit.DAYS, Arrays.asList(new String[]{"ROLE_USER"}));
		
		this.mockMvc.perform(
				get(url).header("Authorization", "Bearer " + token).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	public void shouldPostReturn200ForAValidToken() throws Exception {

		String token = generateTokenWithExpirationDate(1, TimeUnit.DAYS, Arrays.asList(new String[]{"ROLE_USER"}));
		this.mockMvc.perform(post(url).header("Authorization", "Bearer " + token).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}
}
