package org.nextprot.api.core.test.base;

import java.util.Date;

import org.nextprot.api.commons.dbunit.AbstractUnitBaseTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;

/**
 * Base class for dbunit tests using the spring-test-dbunit framework http://springtestdbunit.github.io/
 * Transactions are rollback and dev profile is activated by default
 * Dev profile includes database connection to the dev database
 * 
 * @author dteixeira
 */

@TransactionConfiguration(defaultRollback = true)
@Transactional
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionDbUnitTestExecutionListener.class })
@ContextConfiguration("classpath:spring/core-context.xml")
public abstract class CoreUnitBaseTest extends AbstractUnitBaseTest{

	public boolean todayIsAfter(String date) {
		Date somedate = new Date(date);
		Date now = new Date();
		return now.after(somedate);
		
	}


}
