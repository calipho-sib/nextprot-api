package org.nextprot.api.commons.dbunit;

import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Base class for dbunit tests using the spring-test-dbunit framework http://springtestdbunit.github.io/
 * Transactions are rollback and dev profile is activated by default
 * Dev profile includes database connection to the dev database
 * @RunWith(SpringJUnit4ClassRunner.class)
 @ContextConfiguration("classpath:api-servlet-test.xml")
 @ActiveProfiles("test")

 * @author dteixeira
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({"dev","cache"})
@DirtiesContext
@Deprecated //should not run on a database with real data (doesn't work for unit testing)
public abstract class AbstractIntegrationBaseTest {
	

}
