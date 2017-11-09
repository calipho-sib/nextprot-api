package org.nextprot.api.commons.dbunit;

import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Base class for dbunit tests using the spring-test-dbunit framework http://springtestdbunit.github.io/
 * Transactions are rollback and dev profile is activated by default
 * Dev profile includes database connection to the dev database
 * 
 * @author dteixeira, mpereira
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({"unit", "unit-schema-nextprot"})
@ContextConfiguration("classpath:spring/commons-context.xml")
public abstract class AbstractUnitBaseTest {
	
}
