package org.nextprot.api.web.dbunit.base.mvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.nextprot.api.security.service.JWTCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.ResultActions;

/**
 * Base class for dbunit tests using the spring-test-dbunit framework http://springtestdbunit.github.io/
 * Transactions are rollback and dev profile is activated by default
 * Dev profile includes database connection to the dev database
 * 
 * @author dteixeira
 */

public abstract class MVCBaseSecurityIntegrationTest extends MVCBaseIntegrationTest {

	@Autowired
	protected FilterChainProxy springSecurityFilterChain;

	@Autowired
	private JWTCodec<Object> codec;

	protected String generateTokenWithExpirationDate(int value, TimeUnit time, List<String> roles) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("email", "auth0@test.com");
		map.put("roles", roles);
		return codec.encodeJWT(map, (int) time.toSeconds(value));

	}
	
	protected ResultActions callUrlWithoutToken(String url) throws Exception {
		return this.mockMvc.perform(get(url));
	}

	protected ResultActions callUrlWithToken(String url, String token) throws Exception {
		return this.mockMvc.perform(get(url).header("Authorization", "Bearer " + token));
	}
	

}
