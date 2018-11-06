package org.nextprot.api.solr.index.docfactory.entryfield;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import org.nextprot.api.commons.dbunit.AbstractUnitBaseTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles({"dev", "cache"})
@TransactionConfiguration(defaultRollback = true)
@Transactional
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionDbUnitTestExecutionListener.class })
@ContextConfiguration("classpath:spring/core-context.xml")
public abstract class SolrBuildIntegrationTest extends AbstractUnitBaseTest {

}
