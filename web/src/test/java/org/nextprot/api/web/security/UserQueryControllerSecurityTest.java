package org.nextprot.api.web.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.nextprot.api.user.domain.UserQuery;
import org.nextprot.api.web.dbunit.base.mvc.MVCBaseSecurityTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

public class UserQueryControllerSecurityTest extends MVCBaseSecurityTest {

	/*@Test
	public void shouldReturn200ForPublicQueriesEvenWithoutToken() throws Exception {
		this.mockMvc.perform(get("/queries/public").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}*/

	@Test
	public void shouldPostReturn200ForAValidToken() throws Exception {

		String usr = "test@nextprot.org";
		String token = generateTokenWithExpirationDate(usr, 1, TimeUnit.DAYS, Arrays.asList(new String[] { "ROLE_USER" }));
		
		UserQuery q = new UserQuery();
		q.setTitle("My super query");
		q.setOwner(usr);
		q.setSparql("some sparql");
		
//		String content = new ObjectMapper().writeValueAsString(q);
		
		String content = "{\"userQueryId\":0,\"title\":\"My super query\",\"description\":null,\"sparql\":\"some sparql\",\"published\":false,\"owner\":\"test@nextprot.org\",\"ownerId\":0,\"tags\":null,\"persisted\":false,\"ownerName\":\"test@nextprot.org\"}";

		
		ResultActions action = this.mockMvc.perform(
				post("/user/" + usr + "/query").
				contentType(MediaType.APPLICATION_JSON).
				content(content).
				header("Authorization", "Bearer " + token).
				accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isOk());
		
		System.err.println(action.andReturn().getResponse().getContentAsString());
	}
}
