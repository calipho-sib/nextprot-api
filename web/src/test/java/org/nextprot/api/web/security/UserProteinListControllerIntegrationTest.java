package org.nextprot.api.web.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;
import org.nextprot.api.user.domain.UserProteinList;
import org.nextprot.api.web.dbunit.base.mvc.MVCBaseSecurityTest;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests GET, PUT, POST, DELETE for 3 different scenarios (anonymous, owner and other logged user) 
 *
 * @author fnikitin
 */
@DatabaseSetup(value = "UserProteinListControllerIntegrationTest.xml", type = DatabaseOperation.INSERT)
public class UserProteinListControllerIntegrationTest extends MVCBaseSecurityTest {

	// --------------------------------- POST -------------------------------------------------------------

	@Test
	public void sheldonShouldBeAbleToCreateProteinList() throws Exception {

		String sheldonUser = "Sheldon";
		String sheldonToken = generateTokenWithExpirationDate("Sheldon", 1, TimeUnit.DAYS, Arrays.asList("ROLE_USER"));

		String content = "{\"id\":0,\"name\":\"my list\",\"description\":\"no desc\",\"accessionNumbers\":[\"NX_45465\"],\"entriesCount\":1,\"ownerId\":0,\"owner\":\"sheldon\",\"ownerName\":\"sheldon\"}";

		// call UserProteinList createUserProteinList()
		String responseString = this.mockMvc.perform(post("/user/" + sheldonUser + "/protein-list").contentType(MediaType.APPLICATION_JSON).
				content(content).header("Authorization", "Bearer " + sheldonToken).accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		UserProteinList userProteinList = new ObjectMapper().readValue(responseString, UserProteinList.class);
		assertTrue(userProteinList.getOwnerId() > 1);
		assertTrue(userProteinList.getOwner().equals(sheldonUser));
	}

	@Test
	public void othersShouldBeUnauthorizedToCreateProteinList() throws Exception {

		String content = "{\"id\":0,\"name\":\"my list\",\"description\":\"no desc\",\"accessionNumbers\":[\"NX_45465\"],\"entriesCount\":1,\"ownerId\":0,\"owner\":\"sheldon\",\"ownerName\":\"sheldon\"}";

		// call UserProteinList createUserProteinList()
		this.mockMvc.perform(post("/user/penny/protein-list").contentType(MediaType.APPLICATION_JSON).content(content).accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isUnauthorized());
	}

	// --------------------------------- GET --------------------------------------------------------------


	// --------------------------------- PUT --------------------------------------------------------------


	// --------------------------------- DELETE -----------------------------------------------------------


}
