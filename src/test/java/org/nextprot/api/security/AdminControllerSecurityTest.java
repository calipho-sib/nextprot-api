package org.nextprot.api.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.nextprot.api.dbunit.MVCBaseIntegrationTest;
import org.nextprot.api.user.domain.UserApplication;
import org.springframework.http.MediaType;

public class AdminControllerSecurityTest extends MVCBaseIntegrationTest {

	@Test
	public void shouldAskForCredentials() throws Exception {

		this.mockMvc.perform(post("/user/dani/applications.json").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content("{\"name\":\"name\"}")).andExpect(
				status().isForbidden());

	}

	@Test
	public void showReturn401ForAnInvalidToken() throws Exception {

		this.mockMvc.perform(
				post("/user/dani/applications.json").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer a.b.c").accept(MediaType.APPLICATION_JSON).content("{\"name\":\"name\"}"))
				.andExpect(status().isUnauthorized());

	}

	@Test
	public void showReturn200ForAValidToken() throws Exception {

		String token = generateTestToken();
		
		this.mockMvc.perform(
				post("/user/dani/applications.json").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + token).accept(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"name\"}")).andExpect(status().isOk());
	}
	
	@Test
	public void decodeToken(){
		String tkn = "eyJ0eXBlIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJwYXlsb2FkIjoie1wiaWRcIjpcIlNPTUUtUkFORE9NLUlEXCIsXCJuYW1lXCI6XCJ1bml0LXRlc3QtYXBwbGljYXRpb25cIn0iLCJleHAiOjE0NDEyMDE1NTh9.WzmYPE05bxaP2sn1XMceHfIInzwUXcvillYA_FgoEkw";
		UserApplication a = keyGenerator.decodeToken(tkn);
		System.out.println("name:" + a.getName());
		System.out.println("id:" + a.getId());
		
	}

}
