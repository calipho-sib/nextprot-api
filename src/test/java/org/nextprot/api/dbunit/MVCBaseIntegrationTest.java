package org.nextprot.api.dbunit;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

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

@WebAppConfiguration
@ContextConfiguration("classpath:META-INF/spring/web-context.xml")
@DirtiesContext
public abstract class MVCBaseIntegrationTest extends AbstractIntegrationBaseTest {

	@Autowired
	protected WebApplicationContext wac;

	protected MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = webAppContextSetup(this.wac).build();
	}

}
