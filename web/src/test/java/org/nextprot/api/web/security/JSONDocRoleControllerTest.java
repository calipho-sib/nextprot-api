package org.nextprot.api.web.security;

import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.MVCBaseSecurityTest;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests GET, PUT, POST, DELETE for 3 different scenarios (anonymous, owner and other logged user) 
 * @author dteixeira
 *
 */
public class JSONDocRoleControllerTest extends MVCBaseSecurityTest {

	@Test
	@Ignore
	public void sheldonShouldBeAbleToSeeHisSuperGeniousQuery() throws Exception {

		String sheldonToken = generateTokenWithExpirationDate("Sheldon", 1, TimeUnit.DAYS, Arrays.asList("ROLE_USER"));

		String responseString = this.getJSONDocByUser(sheldonToken);

		// Admin group does not exist
		assertFalse(this.isMatchRegExpGroup(responseString, "Admin"));

		// User and public groups exist
		assertTrue(this.isMatchRegExpGroup(responseString, ""));
		assertTrue(this.isMatchRegExpGroup(responseString, "User"));
		assertTrue(this.isMatchRegExpGroup(responseString, "Protein Lists"));
		assertTrue(this.isMatchRegExpGroup(responseString, "Sparql Queries"));
		
		// Check presence of User subgroups
		
		//user stuff
		assertTrue(this.containsWithKeyValue(responseString, "name", "Protein lists"));
		assertTrue(this.containsWithKeyValue(responseString, "group", "Protein Lists"));

		//public
		assertTrue(this.containsWithKeyValue(responseString, "name", "Queries"));
		assertTrue(this.containsWithKeyValue(responseString, "group", "Sparql Queries"));
	}

	@Test
	@Ignore
	public void adminUserShouldBeAbleToSeeAllData() throws Exception {

		String adminToken = generateTokenWithExpirationDate("AdminUser", 1, TimeUnit.DAYS, Arrays.asList("ROLE_ADMIN", "ROLE_USER"));

		String responseString = this.getJSONDocByUser(adminToken);

		// All groups exist
		assertTrue(this.isMatchRegExpGroup(responseString, ""));
		assertTrue(this.isMatchRegExpGroup(responseString, "Admin"));
		assertTrue(this.isMatchRegExpGroup(responseString, "User"));
		assertTrue(this.isMatchRegExpGroup(responseString, "Protein Lists"));
		assertTrue(this.isMatchRegExpGroup(responseString, "Sparql Queries"));

		// Check presence of User subgroups
		assertTrue(this.containsWithKeyValue(responseString, "name", "Admin tasks"));
		assertTrue(this.containsWithKeyValue(responseString, "name", "User Application"));
	
		//user stuff
		assertTrue(this.containsWithKeyValue(responseString, "name", "Protein lists"));
		assertTrue(this.containsWithKeyValue(responseString, "group", "Protein Lists"));

		//public
		assertTrue(this.containsWithKeyValue(responseString, "name", "User Queries"));
		assertTrue(this.containsWithKeyValue(responseString, "group", "Sparql Queries"));

	}

	@Test
	@Ignore
	public void anonymousShouldBeAbleToSeeSimpleData() throws Exception {

		String adminToken = generateTokenWithExpirationDate("Anonymous", 1, TimeUnit.DAYS, Arrays.asList("ROLE_ANONYMOUS"));

		String responseString = this.getJSONDocByUser(adminToken);

		// Admin group does not exist
		assertFalse(this.isMatchRegExpGroup(responseString, "Admin"));
		// User and "" groups exist 
		assertTrue(this.isMatchRegExpGroup(responseString, "Protein Lists"));
		assertTrue(this.isMatchRegExpGroup(responseString, "Sparql Queries"));


		// Check presence/absence of User subgroups
		assertTrue(this.containsWithKeyValue(responseString, "name", "Protein lists"));
		assertTrue(this.containsWithKeyValue(responseString, "name", "Queries"));


		// Check that does not contain any "modification" verbs
		assertFalse(this.containsWithKeyValue(responseString, "verb", "PATCH"));
		assertFalse(this.containsWithKeyValue(responseString, "verb", "PUT"));
		assertFalse(this.containsWithKeyValue(responseString, "verb", "DELETE"));
		assertFalse(this.containsWithKeyValue(responseString, "verb", "HEAD"));
		assertFalse(this.containsWithKeyValue(responseString, "verb", "OPTIONS"));
		assertFalse(this.containsWithKeyValue(responseString, "verb", "TRACE"));
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
	 * Returns true if and only if the provided string contains the specified string formed by key and value 
	 * (for instance, '"name":"User Application"').
	 */
	private boolean containsWithKeyValue(String string, String key, String value) {
		return doStringMatchRegexpInDotAllMode(".*\"" + key + "\"\\s*:\\s*\"" + value +"\".*", string);
	}
	
	/**
	 * Returns true if and only if the provided string contains the specified string of a JSONDoc group 
	 * (for instance, '"Admin":[' not succeeded by ']').
	 */
	private boolean isMatchRegExpGroup(String string, String groupName) {

		return doStringMatchRegexpInDotAllMode(".*\""+groupName+"\"\\s*:\\s*\\[[^]].*", string);
	}

	private boolean doStringMatchRegexpInDotAllMode(String regexp, String string) {

		Pattern p = Pattern.compile(regexp, Pattern.DOTALL);
		Matcher m = p.matcher(string);

		return m.matches();
	}
}
