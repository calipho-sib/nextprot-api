package org.nextprot.api.web.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.user.controller.PublicQueryController;
import org.nextprot.api.user.controller.UserQueryController;
import org.nextprot.api.user.domain.UserQuery;
import org.nextprot.api.web.dbunit.base.mvc.MVCBaseSecurityTest;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests GET, PUT, POST, DELETE for 3 different scenarios (anonymous, owner and other logged user) 
 * @author dteixeira
 *
 */
@DatabaseSetup(value = "UserQueryControllerIntegrationTest.xml", type = DatabaseOperation.INSERT)
public class UserQueryControllerIntegrationTest extends MVCBaseSecurityTest {

	// --------------------------------- POST -------------------------------------------------------------

	@Test
	@Ignore
	public void sheldonShouldBeAbleToCreateHisSuperGeniousQuery() throws Exception {

		String sheldonUser = "Sheldon";
		String sheldonToken = generateTokenWithExpirationDate("Sheldon", 1, TimeUnit.DAYS, Arrays.asList("ROLE_USER"));

		String content = "{\"userQueryId\":0,\"title\":\"Super Genious Query\",\"description\":null,\"sparql\":\"some sparql\",\"published\":false,\"owner\":\"test@nextprot.org\",\"ownerId\":0,\"tags\":null,\"ownerName\":\"test@nextprot.org\"}";

		// call UserQuery createAdvancedQuery()
		String responseString = this.mockMvc.perform(post("/user/me/queries").with(csrf()).contentType(MediaType.APPLICATION_JSON).
				content(content).header("Authorization", "Bearer " + sheldonToken).accept(MediaType.APPLICATION_JSON)).
				andDo(print()).
				andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		UserQuery uq = new ObjectMapper().readValue(responseString, UserQuery.class);
		assertTrue(uq.getUserQueryId() > 1); //assert that an id was given
		assertTrue(uq.getOwner().equals(sheldonUser));  //asserts that the owner of the query is bob
	}


	@Test
	public void othersShouldNotBeAbleToCreateQuery() throws Exception {

		String content = "{\"userQueryId\":0,\"title\":\"Super Genious Query\",\"description\":null,\"sparql\":\"some sparql\",\"published\":false,\"owner\":\"test@nextprot.org\",\"ownerId\":0,\"tags\":null,\"ownerName\":\"test@nextprot.org\"}";

		// call UserQuery createAdvancedQuery()
		this.mockMvc.perform(post("/user/me/queries").with(csrf()).contentType(MediaType.APPLICATION_JSON).
				content(content).accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isUnauthorized());
	}

	// --------------------------------- GET --------------------------------------------------------------

	@Test
	public void anybodyShouldBeAbleToLookAtTutorialEvenWithoutToken() throws Exception {

		// call List<UserQuery> getTutorialQueries()
		this.mockMvc.perform(get("/queries/tutorial").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(handler().handlerType(PublicQueryController.class));
	}

	@Test
	@Ignore
	public void leonardShouldBeAbleToLookAtTutorial() throws Exception {

		String leonardToken = generateTokenWithExpirationDate("leonard", 1, TimeUnit.DAYS, Arrays.asList("ROLE_USER"));

		// call List<UserQuery> getTutorialQueries()
		this.mockMvc.perform(get("/user/me/queries").header("Authorization", "Bearer " + leonardToken)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(handler().handlerType(UserQueryController.class));
	}

	@Test
	@Ignore
	public void leonardShouldBeAbleToLookAtHisQueries() throws Exception {

		String leonardToken = generateTokenWithExpirationDate("leonard", 1, TimeUnit.DAYS, Arrays.asList("ROLE_USER"));

		// List<UserQuery> getUserQueries()
		String responseString = this.mockMvc.perform(get("/user/me/queries").
				header("Authorization", "Bearer " + leonardToken).accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		List<UserQuery> uqs = new ObjectMapper().readValue(responseString, new TypeReference<List<UserQuery>>() { });

		assertTrue(!uqs.isEmpty());
		assertEquals(uqs.size(), 1);
		assertEquals(123456789, uqs.get(0).getUserQueryId());
	}

	@Test
	@Ignore
	public void leonardShouldBeAbleToLookAtHisQuery() throws Exception {

		String leonardToken = generateTokenWithExpirationDate("leonard", 1, TimeUnit.DAYS, Arrays.asList("ROLE_USER"));

		// UserQuery getUserQuery()
		String responseString = this.mockMvc.perform(get("/user/me/queries/123456789").
				header("Authorization", "Bearer " + leonardToken).accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		UserQuery uq = new ObjectMapper().readValue(responseString, UserQuery.class);

		assertTrue(uq.getUserQueryId() == 123456789); //assert that an id was given
		assertTrue(uq.getOwner().equals("leonard"));  //asserts that the owner of the query is bob
	}

	/* not applicable anymore 
	@Test
	public void sheldonShouldNotBeAbleToLookAtLeonardsQueries() throws Exception {

		String sheldonToken = generateTokenWithExpirationDate("sheldon", 1, TimeUnit.DAYS, Arrays.asList("ROLE_USER"));

		// UserQuery getUserQuery()
		this.mockMvc.perform(get("/user/me/queries").
				header("Authorization", "Bearer " + sheldonToken).accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isForbidden());
	}*/

	@Test
	@Ignore
	public void sheldonShouldNotBeAbleToLookAtLeonardsQueryByItsPrivateId() throws Exception {

		//Queries can be read by any people, if queries must be kept secret, we could use the approach like for Google Docs (generate a random ID) that can be used on the URL
		
		String sheldonToken = generateTokenWithExpirationDate("sheldon", 1, TimeUnit.DAYS, Arrays.asList("ROLE_USER"));

		// UserQuery getUserQuery()
		this.mockMvc.perform(get("/user/me/queries/123456789").
				header("Authorization", "Bearer " + sheldonToken).accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isForbidden());
	}


	@Test
	@Ignore
	public void sheldonShouldBeAbleToLookAtLeonardsQueryByItsPublicId() throws Exception {

		//Queries can be read by any people, if queries must be kept secret, we could use the approach like for Google Docs (generate a random ID) that can be used on the URL
		
		String sheldonToken = generateTokenWithExpirationDate("sheldon", 1, TimeUnit.DAYS, Arrays.asList("ROLE_USER"));

		// UserQuery getUserQuery()
		this.mockMvc.perform(get("/queries/Abc1").
				header("Authorization", "Bearer " + sheldonToken).accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isOk());
	}

	@Test
	public void othersShouldBeAbleToLookAtLeonardsQueryByItsPublicId() throws Exception {

		this.mockMvc.perform(get("/queries/Abc1").accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isOk());
	}
	
	@Test
	public void othersShouldNotBeAbleToLookAtLeonardsQueryByItsPrivateId() throws Exception {

		//Queries can be read by any people, if queries must be kept secret, we could use the approach like for Google Docs (generate a random ID) that can be used on the URL
		this.mockMvc.perform(get("/user/me/queries/123456789").accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isUnauthorized());
	}

	/* @Test not applicable anymore
	public void othersShouldNotBeAbleToLookAtLeonardsQueries() throws Exception {

		// List<UserQuery> getUserQueries()
		this.mockMvc.perform(get("/user/me/queries").accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isForbidden());
	}*/

	// --------------------------------- PUT --------------------------------------------------------------

	@Test
	@Ignore
	public void leonardShouldBeAbleToUpdateHisQuery() throws Exception {

		String leonardToken = generateTokenWithExpirationDate("leonard", 1, TimeUnit.DAYS, Arrays.asList("ROLE_USER"));

		String content = "{\"userQueryId\":123456789,\"title\":\"Awesomely Genious Query\",\"description\":null,\"sparql\":\"some sparql\",\"published\":false,\"owner\":\"test@nextprot.org\",\"ownerId\":0,\"tags\":null,\"ownerName\":\"test@nextprot.org\"}";

		// UserQuery updateAdvancedQuery()
		String responseString = this.mockMvc.perform(put("/user/me/queries/123456789").with(csrf()).header("Authorization", "Bearer " + leonardToken)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		UserQuery uq = new ObjectMapper().readValue(responseString, UserQuery.class);

		assertEquals("Awesomely Genious Query", uq.getTitle());
		assertTrue(uq.getOwner().equals("leonard"));
	}

	@Test
	@Ignore
	public void leonardShouldNotBeAbleToUpdateATutorialQuery() throws Exception {

		String leonardToken = generateTokenWithExpirationDate("leonard", 1, TimeUnit.DAYS, Arrays.asList("ROLE_USER"));

		String content = "{\"userQueryId\":1,\"title\":\"Awesomely Genious Query\",\"description\":null,\"sparql\":\"some sparql\",\"published\":false,\"owner\":\"test@nextprot.org\",\"ownerId\":0,\"tags\":null,\"ownerName\":\"test@nextprot.org\"}";

		// UserQuery updateAdvancedQuery()
		this.mockMvc.perform(put("/user/me/queries/1").header("Authorization", "Bearer " + leonardToken)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isForbidden());
	}
	
	@Test
	@Ignore
	public void sheldonShouldNotBeAbleToUpdateLeonardsQuery() throws Exception {

		String sheldonToken = generateTokenWithExpirationDate("sheldon", 1, TimeUnit.DAYS, Arrays.asList("ROLE_USER"));

		String content = "{\"userQueryId\":123456789,\"title\":\"Awesomely Genious Query\",\"description\":null,\"sparql\":\"some sparql\",\"published\":false,\"owner\":\"test@nextprot.org\",\"ownerId\":0,\"tags\":null,\"ownerName\":\"test@nextprot.org\"}";

		// UserQuery updateAdvancedQuery()
		this.mockMvc.perform(put("/user/me/queries/123456789").header("Authorization", "Bearer " + sheldonToken)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(content)).
				andExpect(status().isForbidden());
	}

	@Test
	@Ignore
	public void sheldonShouldNotBeAbleToUpdateLeonardsQuery2() throws Exception {

		String sheldonToken = generateTokenWithExpirationDate("sheldon", 1, TimeUnit.DAYS, Arrays.asList("ROLE_USER"));

		String content = "{\"userQueryId\":123456789,\"title\":\"Awesomely Genious Query 1st attempt\",\"description\":null,\"sparql\":\"some sparql\",\"published\":false,\"owner\":\"sheldon\",\"ownerId\":23,\"tags\":null,\"ownerName\":\"sheldon\"}";

		// UserQuery updateAdvancedQuery()
		this.mockMvc.perform(put("/user/me/queries/123456789").header("Authorization", "Bearer " + sheldonToken)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(content)).
				andExpect(status().isForbidden());
	}

	@Test
	public void othersShouldNotBeAbleToUpdateLeonardsQuery() throws Exception {

		String content = "{\"userQueryId\":123456789,\"title\":\"Awesomely Genious Query 2nd attempt\",\"description\":null,\"sparql\":\"some sparql\",\"published\":false,\"owner\":\"test@nextprot.org\",\"ownerId\":0,\"tags\":null,\"ownerName\":\"test@nextprot.org\"}";

		// UserQuery updateAdvancedQuery()
		this.mockMvc.perform(put("/user/me/queries/123456789").with(csrf())
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(content)).
				andExpect(status().isUnauthorized());
	}

	// --------------------------------- DELETE -----------------------------------------------------------

	@Test
	@Ignore
	public void leonardShouldNotBeAbleToDeleteATutorialQuery() throws Exception {

		String leonardToken = generateTokenWithExpirationDate("leonard", 1, TimeUnit.DAYS, Arrays.asList("ROLE_USER"));

		// UserQuery updateAdvancedQuery()
		this.mockMvc.perform(delete("/user/me/queries/1").header("Authorization", "Bearer " + leonardToken)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
	}
	
	@Test
	@Ignore
	public void leonardShouldBeAbleToDeleteHisQuery() throws Exception {

		String leonardToken = generateTokenWithExpirationDate("leonard", 1, TimeUnit.DAYS, Arrays.asList("ROLE_USER"));

		// void deleteUserQuery()
		this.mockMvc.perform(delete("/user/me/queries/123456789").with(csrf()).header("Authorization", "Bearer " + leonardToken)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	@Ignore
	public void sheldonShouldNotBeAbleToDeleteLeonardsQuery() throws Exception {

		String sheldonToken = generateTokenWithExpirationDate("sheldon", 1, TimeUnit.DAYS, Arrays.asList("ROLE_USER"));

		// void deleteUserQuery()
		this.mockMvc.perform(delete("/user/me/queries/123456789").header("Authorization", "Bearer " + sheldonToken)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
	}

	@Test
	public void othersShouldNotBeAbleToDeleteLeonardsQuery() throws Exception {

		// void deleteUserQuery()
		this.mockMvc.perform(delete("/user/me/queries/123456789").with(csrf())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());
	}
}
