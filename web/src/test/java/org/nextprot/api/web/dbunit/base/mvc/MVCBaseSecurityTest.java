package org.nextprot.api.web.dbunit.base.mvc;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.nextprot.api.security.service.JWTCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Base class for dbunit tests using the spring-test-dbunit framework http://springtestdbunit.github.io/
 * Transactions are rollback and dev profile is activated by default
 * Dev profile includes database connection to the dev database
 * 
 * @author dteixeira
 */

@WebAppConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,   DirtiesContextTestExecutionListener.class,    TransactionDbUnitTestExecutionListener.class })
@ContextConfiguration("classpath:META-INF/spring/web-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({"unit",  "unit-schema-user", "security"})
@DirtiesContext
public abstract class MVCBaseSecurityTest {

	@Autowired
	protected WebApplicationContext wac;

	@Autowired
	private FilterChainProxy springSecurityFilterChain;

	protected MockMvc mockMvc;

	@Autowired
	private JWTCodec<Map<String, Object>> codec;

	@Before
	public void setup() {
		this.mockMvc = webAppContextSetup(this.wac).addFilters(this.springSecurityFilterChain).build();
	}


	protected String generateTokenWithExpirationDate(String email, int value, TimeUnit time, List<String> roles) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("email", email);
		map.put("authorities", roles);

		return codec.encodeJWT(map, (int) time.toSeconds(value));

	}
	
	protected ResultActions callUrlWithoutToken(String url) throws Exception {
		return this.mockMvc.perform(get(url));
	}

	protected ResultActions callUrlWithToken(String url, String token) throws Exception {
		return this.mockMvc.perform(get(url).header("Authorization", "Bearer " + token));
	}
	

}
