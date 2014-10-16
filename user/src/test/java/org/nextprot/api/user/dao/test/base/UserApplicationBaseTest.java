package org.nextprot.api.user.dao.test.base;

import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.dataset.DataSetLoader;
import org.dbunit.util.fileloader.FlatXmlDataFileLoader;
import org.nextprot.api.commons.dbunit.AbstractUnitBaseTest;
import org.nextprot.api.commons.dbunit.DTDIgnoredFlatXMLDataSet;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
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
@ContextConfiguration("classpath:spring/commons-context.xml")
@ActiveProfiles({"unit", "unit-schema-user"})
@DbUnitConfiguration(dataSetLoader = DTDIgnoredFlatXMLDataSet.class)
public abstract class UserApplicationBaseTest extends AbstractUnitBaseTest { //implements InitializingBean {

}
