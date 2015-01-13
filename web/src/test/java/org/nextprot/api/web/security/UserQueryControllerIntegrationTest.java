package org.nextprot.api.web.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;
import org.nextprot.api.user.controller.UserQueryController;
import org.nextprot.api.user.domain.UserQuery;
import org.nextprot.api.web.dbunit.base.mvc.MVCBaseSecurityTest;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests GET, PUT, POST, DELETE for 3 different scenarios (anonymous, owner and other logged user) 
 * @author dteixeira
 *
 */
@DatabaseSetup(value = "UserQueryControllerIntegrationTest.xml", type = DatabaseOperation.INSERT)
public class UserQueryControllerIntegrationTest extends MVCBaseSecurityTest {

	@Test
	public void shouldReturn200ForPublicQueriesEvenWithoutToken() throws Exception {

		this.mockMvc.perform(get("/queries/public").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(handler().handlerType(UserQueryController.class));
	}

	@Test
	public void sheldonShouldBeAbleToCreateHisSuperGeniousQuery() throws Exception {

		String sheldonUser = "Sheldon";
		String sheldonToken = generateTokenWithExpirationDate("Sheldon", 1, TimeUnit.DAYS, Arrays.asList(new String[] { "ROLE_USER" }));

		String content = "{\"userQueryId\":0,\"title\":\"Super Genious Query\",\"description\":null,\"sparql\":\"some sparql\",\"published\":false,\"owner\":\"test@nextprot.org\",\"ownerId\":0,\"tags\":null,\"persisted\":false,\"ownerName\":\"test@nextprot.org\"}";

		String responseString = this.mockMvc.perform(post("/user/" + sheldonUser + "/query").contentType(MediaType.APPLICATION_JSON).
				content(content).header("Authorization", "Bearer " + sheldonToken).accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		UserQuery uq = new ObjectMapper().readValue(responseString, UserQuery.class);
		
		assertTrue(uq.getUserQueryId() > 1); //assert that an id was given
		assertTrue(uq.getOwner().equals(sheldonUser));  //asserts that the owner of the query is bob

	}

	// --------------------------------- GET --------------------------------------------------------------
	
	@Test
	public void nobodyShouldBeAbleToLookAtLeonardsQuery() throws Exception {

		this.mockMvc.perform(get("/user/leonard/query/15").accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isForbidden());


	}
	
	@Test
	public void sheldonShouldNotBeAbleToLookAtLeonardsQuery() throws Exception {
		
		String sheldonToken = generateTokenWithExpirationDate("sheldon", 1, TimeUnit.DAYS, Arrays.asList(new String[] { "ROLE_USER" }));

		this.mockMvc.perform(get("/user/leonard/query/15").
				header("Authorization", "Bearer " + sheldonToken).accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isForbidden());


	}
	
	@Test
	public void leonardShouldBeAbleToLookAtHisQuery() throws Exception {
		
		String leonardToken = generateTokenWithExpirationDate("leonard", 1, TimeUnit.DAYS, Arrays.asList(new String[] { "ROLE_USER" }));

		String responseString = this.mockMvc.perform(get("/user/leonard/query/15").
				header("Authorization", "Bearer " + leonardToken).accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		

		UserQuery uq = new ObjectMapper().readValue(responseString, UserQuery.class);
		
		assertTrue(uq.getUserQueryId() == 15); //assert that an id was given
		assertTrue(uq.getOwner().equals("leonard"));  //asserts that the owner of the query is bob


	}
	
	
}
