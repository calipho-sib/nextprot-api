package org.nextprot.api.web.security;

import static org.junit.Assert.assertTrue;
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

		System.out.println(responseString);
		
		// Admin groups exists but is empty (for instance, "Admin":[]")
		assertTrue(responseString.matches(this.getRegExpForEmptyGroup("Admin")));
		// [^\\]] = Matches any single character but not ']'
		assertTrue(responseString.matches(this.getRegExpForNotEmptyGroup("User")));
		assertTrue(responseString.matches(this.getRegExpForNotEmptyGroup("")));
	}

	@Test
	public void adminShouldBeAbleToSeeAllData() throws Exception {

		String adminToken = generateTokenWithExpirationDate("Admin", 1, TimeUnit.DAYS, Arrays.asList("ROLE_ADMIN"));

		String responseString = this.getJSONDocByUser(adminToken);

		System.out.println(responseString);

		// [^\\]] = Matches any single character but not ']'
		assertTrue(responseString.matches(this.getRegExpForNotEmptyGroup("Admin")));
		assertTrue(responseString.matches(this.getRegExpForNotEmptyGroup("User")));
		assertTrue(responseString.matches(this.getRegExpForNotEmptyGroup("")));
	}

	@Test
	public void anonymousShouldBeAbleToSeeSimpleData() throws Exception {

		String adminToken = generateTokenWithExpirationDate("Anonymous", 1, TimeUnit.DAYS, Arrays.asList("ROLE_ANONYMOUS"));

		String responseString = this.getJSONDocByUser(adminToken);

		System.out.println(responseString);

		// Admin and User groups exists but are empty
		assertTrue(responseString.matches(this.getRegExpForEmptyGroup("Admin")));
		assertTrue(responseString.matches(this.getRegExpForEmptyGroup("User")));
		// [^\\]] = Matches any single character but not ']'
		assertTrue(responseString.matches(this.getRegExpForNotEmptyGroup("")));
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
	 * Get regular expression to be able to match empty JSONDoc group (for instance, if <code>groupName</code>
	 * equals "Admin" we want to be able to match "Admin":[]").
	 */
	private String getRegExpForEmptyGroup(String groupName) {
		return ".*\""+groupName+"\":\\[\\].*";
	}
	
	/**
	 * Get regular expression to be able to match <b>not</b> empty JSONDoc group (for instance, if <code>groupName</code>
	 * equals "User" we do not want to be able to match "Admin":[]
	 */
	private String getRegExpForNotEmptyGroup(String groupName) {
		return ".*\""+groupName+"\":\\[[^\\]].*";
	}
}
