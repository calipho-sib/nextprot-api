package org.nextprot.api.dbunit;

import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Base class for dbunit tests using the spring-test-dbunit framework http://springtestdbunit.github.io/
 * Transactions are rollback and dev profile is activated by default
 * Dev profile includes database connection to the dev database
 * 
 * @author dteixeira, mpereira
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("unit")
abstract class AbstractUnitBaseTest {
	
}
