package org.nextprot.api.web.security;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.MVCBaseSecurityTest;
import org.springframework.http.MediaType;

/**
 * Tests GET, PUT, POST, DELETE for 3 different scenarios (anonymous, owner and other logged user) 
 * @author dteixeira
 *
 */
public class JSONDocRoleControllerTest extends MVCBaseSecurityTest {

	@Test
	public void sheldonShouldBeAbleToSeeHisSuperGeniousQuery() throws Exception {

		String sheldonToken = generateTokenWithExpirationDate("Sheldon", 1, TimeUnit.DAYS, Arrays.asList("ROLE_USER"));

		String responseString = this.getJSONDocByUser(sheldonToken);

		// Admin group does not exist
		assertFalse(responseString.matches(this.getRegExpGroup("Admin")));
		// User and "" groups exist
		assertTrue(responseString.matches(this.getRegExpGroup("User")));
		assertTrue(responseString.matches(this.getRegExpGroup("")));
	}

	@Test
	public void adminShouldBeAbleToSeeAllData() throws Exception {

		String adminToken = generateTokenWithExpirationDate("Admin", 1, TimeUnit.DAYS, Arrays.asList("ROLE_ADMIN"));

		String responseString = this.getJSONDocByUser(adminToken);

		// All groups exist
		assertTrue(responseString.matches(this.getRegExpGroup("Admin")));
		assertTrue(responseString.matches(this.getRegExpGroup("User")));
		assertTrue(responseString.matches(this.getRegExpGroup("")));
	}

	@Test
	public void anonymousShouldBeAbleToSeeSimpleData() throws Exception {

		String adminToken = generateTokenWithExpirationDate("Anonymous", 1, TimeUnit.DAYS, Arrays.asList("ROLE_ANONYMOUS"));

		String responseString = this.getJSONDocByUser(adminToken);

		// Admin and User groups do not exist
		assertFalse(responseString.matches(this.getRegExpGroup("Admin")));
		assertFalse(responseString.matches(this.getRegExpGroup("User")));
		// "" group exist
		assertTrue(responseString.matches(this.getRegExpGroup("")));
	}

	/**
	 * Get MVC mock for jsondoc with the providen user.
	 */
	private String getJSONDocByUser(String user) throws Exception {
		return this.mockMvc.perform(get("/jsondoc").contentType(MediaType.APPLICATION_JSON).
				header("Authorization", "Bearer " + user).accept(MediaType.APPLICATION_JSON)).
				andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
	}
	
	/**
	 * Get regular expression to be able to match JSONDoc group.
	 */
	private String getRegExpGroup(String groupName) {
		return ".*\""+groupName+"\":\\[[^\\]].*";
	}
}
