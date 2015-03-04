package org.nextprot.api.web.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.nextprot.api.user.domain.UserProteinList;
import org.nextprot.api.web.dbunit.base.mvc.MVCBaseSecurityTest;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
	// --------------------------------- GET PROTEINS LISTS -----------------------------------------------
	@Test
	public void leonardShouldBeAbleToLookAtHisOwnProteinLists() throws Exception {

		String leonardToken = generateTokenWithExpirationDate("leonard", 1, TimeUnit.DAYS, Arrays.asList("ROLE_USER"));

		// call List<UserProteinList> getUserProteinLists()
		String responseString = this.mockMvc.perform(get("/user/leonard/protein-list").header("Authorization", "Bearer " + leonardToken)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		List<UserProteinList> list = new ObjectMapper().readValue(responseString, new TypeReference<List<UserProteinList>>() { });

		assertTrue(!list.isEmpty());
		assertEquals(2, list.size());
		assertTrue(list.get(0).getAccessionNumbers().isEmpty());
		assertTrue(list.get(1).getAccessionNumbers().isEmpty());
		assertEquals(23, list.get(0).getOwnerId());
		assertEquals(23, list.get(1).getOwnerId());
	}

	@Test
	public void sheldonIsForbiddenToLookAtLeonardsProteinLists() throws Exception {

		String sheldonToken = generateTokenWithExpirationDate("sheldon", 1, TimeUnit.DAYS, Arrays.asList("ROLE_USER" ));

		// call List<UserProteinList> getUserProteinLists()
		this.mockMvc.perform(get("/user/leonard/protein-list").
				header("Authorization", "Bearer " + sheldonToken).accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isForbidden());
	}

	@Test
	public void othersAreUnauthorizedToLookAtLeonardsProteinLists() throws Exception {

		// call List<UserProteinList> getUserProteinLists()
		this.mockMvc.perform(get("/user/leonard/protein-list").accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isForbidden());
	}

	// --------------------------------- GET PROTEINS LIST ------------------------------------------------

	@Test
	public void leonardShouldBeAbleToLookAtHisOwnProteinList() throws Exception {

		String leonardToken = generateTokenWithExpirationDate("leonard", 1, TimeUnit.DAYS, Arrays.asList("ROLE_USER"));

		// call UserProteinList getUserProteinList()
		String responseString = this.mockMvc.perform(get("/user/leonard/protein-list/leonardslist1").header("Authorization", "Bearer " + leonardToken)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		UserProteinList userProteinList = new ObjectMapper().readValue(responseString, new TypeReference<UserProteinList>() { });

		assertEquals(23, userProteinList.getOwnerId());
		assertEquals(Sets.newHashSet("NX_Q14239","NX_Q8N5Z0","NX_P05185"), userProteinList.getAccessionNumbers());
	}

	@Test
	public void leonardShouldBeAbleToLookAtUnfoundProteinList() throws Exception {

		String leonardToken = generateTokenWithExpirationDate("leonard", 1, TimeUnit.DAYS, Arrays.asList("ROLE_USER"));

		// call Set<String> getUserProteinListAccessionNumbers()
		this.mockMvc.perform(get("/user/leonard/protein-list/unknown-list").header("Authorization", "Bearer " + leonardToken)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	public void sheldonIsForbiddenToLookAtLeonardsProteinList() throws Exception {

		String sheldonToken = generateTokenWithExpirationDate("sheldon", 1, TimeUnit.DAYS, Arrays.asList("ROLE_USER" ));

		// call UserProteinList getUserProteinList()
		this.mockMvc.perform(get("/user/leonard/protein-list/leonardslist1").
				header("Authorization", "Bearer " + sheldonToken).accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isForbidden());
	}

	@Test
	public void othersAreUnauthorizedToLookAtLeonardsProteinList() throws Exception {

		// call UserProteinList getUserProteinList()
		this.mockMvc.perform(get("/user/leonard/protein-list/leonardslist1").accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isForbidden());
	}

	// --------------------------------- GET PROTEINS ACC NUMBERS -----------------------------------------

	@Test
	public void leonardShouldBeAbleToLookAtHisOwnProteinListAccessionNumbers() throws Exception {

		String leonardToken = generateTokenWithExpirationDate("leonard", 1, TimeUnit.DAYS, Arrays.asList("ROLE_USER"));

		// call Set<String> getUserProteinListAccessionNumbers()
		String responseString = this.mockMvc.perform(get("/user/leonard/protein-list/leonardslist1/accnums").header("Authorization", "Bearer " + leonardToken)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		Set<String> accessionNumbers = new ObjectMapper().readValue(responseString, new TypeReference<Set<String>>() { });

		assertEquals(Sets.newHashSet("NX_Q14239","NX_Q8N5Z0","NX_P05185"), accessionNumbers);
	}

	@Test
	public void sheldonIsForbiddenToLookAtLeonardsProteinListAccessionNumbers() throws Exception {

		String sheldonToken = generateTokenWithExpirationDate("sheldon", 1, TimeUnit.DAYS, Arrays.asList("ROLE_USER" ));

		// call Set<String> getUserProteinListAccessionNumbers()
		this.mockMvc.perform(get("/user/leonard/protein-list/leonardslist1/accnums").
				header("Authorization", "Bearer " + sheldonToken).accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isForbidden());
	}

	@Test
	public void othersAreUnauthorizedToLookAtLeonardsProteinListAccessionNumbers() throws Exception {

		// call Set<String> getUserProteinListAccessionNumbers()
		this.mockMvc.perform(get("/user/leonard/protein-list/leonardslist/accnums").accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isForbidden());
	}

	// --------------------------------- GET COMBINED PROTEIN LIST ----------------------------------------

	@Test
	public void leonardShouldBeAbleToAndCombineProteinLists() throws Exception {

		String leonardToken = generateTokenWithExpirationDate("leonard", 1, TimeUnit.DAYS, Arrays.asList("ROLE_USER"));

		// call UserProteinList combineUserProteinList()
		String responseString = this.mockMvc.perform(get("/user/leonard/protein-list/combine?listname=leonardslist3&listname1=leonardslist1&listname2=leonardslist2&op=AND")
				.header("Authorization", "Bearer " + leonardToken)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		UserProteinList userProteinList = new ObjectMapper().readValue(responseString, new TypeReference<UserProteinList>() { });

		assertEquals(23, userProteinList.getOwnerId());
		assertEquals(Sets.newHashSet("NX_Q8N5Z0"), userProteinList.getAccessionNumbers());
	}

	@Test
	public void leonardShouldBeAbleToOrCombineProteinLists() throws Exception {

		String leonardToken = generateTokenWithExpirationDate("leonard", 1, TimeUnit.DAYS, Arrays.asList("ROLE_USER"));

		// call UserProteinList combineUserProteinList()
		String responseString = this.mockMvc.perform(get("/user/leonard/protein-list/combine?listname=leonardslist3&listname1=leonardslist1&listname2=leonardslist2&op=OR")
				.header("Authorization", "Bearer " + leonardToken)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		UserProteinList userProteinList = new ObjectMapper().readValue(responseString, new TypeReference<UserProteinList>() { });

		assertEquals(23, userProteinList.getOwnerId());
		assertEquals(Sets.newHashSet("NX_Q14239","NX_Q8N5Z0","NX_P05185", "NX_Q14249","NX_P05165"), userProteinList.getAccessionNumbers());
	}

	@Test
	public void leonardShouldBeAbleToNotInCombineProteinLists() throws Exception {

		String leonardToken = generateTokenWithExpirationDate("leonard", 1, TimeUnit.DAYS, Arrays.asList("ROLE_USER"));

		// call UserProteinList combineUserProteinList()
		String responseString = this.mockMvc.perform(get("/user/leonard/protein-list/combine?listname=leonardslist3&listname1=leonardslist1&listname2=leonardslist2&op=NOT_IN")
				.header("Authorization", "Bearer " + leonardToken)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		UserProteinList userProteinList = new ObjectMapper().readValue(responseString, new TypeReference<UserProteinList>() { });

		assertEquals(23, userProteinList.getOwnerId());
		assertEquals(Sets.newHashSet("NX_Q14239","NX_P05185"), userProteinList.getAccessionNumbers());
	}

	@Test
	public void sheldonIsForbiddenToCombineLeonardsProteinLists() throws Exception {

		String sheldonToken = generateTokenWithExpirationDate("sheldon", 1, TimeUnit.DAYS, Arrays.asList("ROLE_USER" ));

		// call Set<String> getUserProteinListAccessionNumbers()
		this.mockMvc.perform(get("/user/leonard/protein-list/combine?listname=leonardslist3&listname1=leonardslist1&listname2=leonardslist2&op=AND").
				header("Authorization", "Bearer " + sheldonToken).accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isForbidden());
	}

	@Test
	public void othersAreUnauthorizedToCombineLeonardsProteinLists() throws Exception {

		// call Set<String> getUserProteinListAccessionNumbers()
		this.mockMvc.perform(get("/user/leonard/protein-list/combine?listname=leonardslist3&listname1=leonardslist1&listname2=leonardslist2&op=AND").
				accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isUnauthorized());
	}

	// --------------------------------- PUT --------------------------------------------------------------

	@Test
	public void leonardShouldBeAbleToUpdateHisProteinList() throws Exception {

		String leonardToken = generateTokenWithExpirationDate("leonard", 1, TimeUnit.DAYS, Arrays.asList("ROLE_USER"));

		String content = "{\"id\":0,\"name\":\"leonardslist10\",\"description\":\"no desc\",\"accessionNumbers\":[\"NX_45465\"],\"entriesCount\":1,\"ownerId\":0,\"owner\":\"leonard\",\"ownerName\":\"leonard\"}";

		// UserProteinList updateUserProteinListMetadata()
		String responseString = this.mockMvc.perform(put("/user/leonard/protein-list/157").header("Authorization", "Bearer " + leonardToken)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		UserProteinList userProteinList = new ObjectMapper().readValue(responseString, new TypeReference<UserProteinList>() { });

		assertEquals(23, userProteinList.getOwnerId());
		assertEquals(Sets.newHashSet("NX_45465"), userProteinList.getAccessionNumbers());
	}

	@Test
	public void sheldonIsForbiddenToUpdateLeonardsProteinList() throws Exception {

		String sheldonToken = generateTokenWithExpirationDate("sheldon", 1, TimeUnit.DAYS, Arrays.asList("ROLE_USER"));

		String content = "{\"id\":0,\"name\":\"leonardslist10\",\"description\":\"no desc\",\"accessionNumbers\":[\"NX_45465\"],\"entriesCount\":1,\"ownerId\":0,\"owner\":\"leonard\",\"ownerName\":\"leonard\"}";

		// UserProteinList updateUserProteinListMetadata()
		this.mockMvc.perform(put("/user/leonard/protein-list/157").header("Authorization", "Bearer " + sheldonToken)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(content)).
				andExpect(status().isForbidden());
	}

	@Test
	public void othersIsUnauthorizedToUpdateLeonardsProteinList() throws Exception {

		String content = "{\"id\":0,\"name\":\"leonardslist10\",\"description\":\"no desc\",\"accessionNumbers\":[\"NX_45465\"],\"entriesCount\":1,\"ownerId\":0,\"owner\":\"leonard\",\"ownerName\":\"leonard\"}";

		// UserProteinList updateUserProteinListMetadata()
		this.mockMvc.perform(put("/user/leonard/protein-list/157")
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(content)).
				andExpect(status().isUnauthorized());
	}

	// --------------------------------- DELETE -----------------------------------------------------------

	@Test
	public void leonardShouldBeAbleToDeleteHisProteinList() throws Exception {

		String leonardToken = generateTokenWithExpirationDate("leonard", 1, TimeUnit.DAYS, Arrays.asList("ROLE_USER"));

		// void deleteUserProteinList()
		this.mockMvc.perform(delete("/user/leonard/protein-list/157").header("Authorization", "Bearer " + leonardToken)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void sheldonIsForbiddenToDeleteLeonardsProteinList() throws Exception {

		String sheldonToken = generateTokenWithExpirationDate("sheldon", 1, TimeUnit.DAYS, Arrays.asList("ROLE_USER"));

		// void deleteUserProteinList()
		this.mockMvc.perform(delete("/user/leonard/protein-list/157").header("Authorization", "Bearer " + sheldonToken)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
	}

	@Test
	public void othersIsUnauthorizedToDeleteLeonardsProteinList() throws Exception {

		// void deleteUserProteinList()
		this.mockMvc.perform(delete("/user/leonard/protein-list/157")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());
	}
}
