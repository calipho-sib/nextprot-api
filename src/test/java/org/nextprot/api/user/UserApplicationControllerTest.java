package org.nextprot.api.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.nextprot.api.dbunit.MVCBaseSecurityIntegrationTest;
import org.springframework.http.MediaType;

public class UserApplicationControllerTest extends MVCBaseSecurityIntegrationTest{

	@Test
	public void shouldCreateAUserApplication() throws Exception {
		
		String token = generateTokenWithExpirationDate(1, TimeUnit.DAYS);
		this.mockMvc.perform(post("/user/applications").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + token).accept(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"name\"}")).andExpect(status().isOk());

	}

}
