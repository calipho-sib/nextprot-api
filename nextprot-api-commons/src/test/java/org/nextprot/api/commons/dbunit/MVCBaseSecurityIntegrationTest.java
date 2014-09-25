package org.nextprot.api.commons.dbunit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.ResultActions;

import sib.calipho.spring.security.auth0.Auth0TokenHelper;

/**
 * Base class for dbunit tests using the spring-test-dbunit framework http://springtestdbunit.github.io/
 * Transactions are rollback and dev profile is activated by default
 * Dev profile includes database connection to the dev database
 * 
 * @RunWith(SpringJUnit4ClassRunner.class)
 * @ContextConfiguration("classpath:api-servlet-test.xml")
 * @ActiveProfiles("test")
 * @author dteixeira
 */

public abstract class MVCBaseSecurityIntegrationTest extends MVCBaseIntegrationTest {
	

	@Autowired
	protected FilterChainProxy springSecurityFilterChain;

	@Autowired
	private Auth0TokenHelper<Object> tokenHelper;


	@Before
	public void setup() {
		this.mockMvc = webAppContextSetup(this.wac).addFilters(this.springSecurityFilterChain).build();
	}
	
	protected String generateTokenWithExpirationDate(int value, TimeUnit time, List<String> roles) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("email", "auth0@test.com");
		map.put("roles", roles);
		return tokenHelper.generateToken(map, (int) time.toSeconds(value));

	}
	
	protected ResultActions callUrlWithoutToken(String url) throws Exception {
		return this.mockMvc.perform(get(url));
	}

	protected ResultActions callUrlWithToken(String url, String token) throws Exception {
		return this.mockMvc.perform(get(url).header("Authorization", "Bearer " + token));
	}
	

}
