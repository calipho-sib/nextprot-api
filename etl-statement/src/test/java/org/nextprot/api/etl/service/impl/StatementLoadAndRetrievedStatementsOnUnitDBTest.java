package org.nextprot.api.etl.service.impl;

import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nextprot.api.etl.service.StatementETLService;
import org.nextprot.api.etl.service.StatementExtractorService;
import org.nextprot.api.etl.service.impl.StatementETLServiceImpl.ReportBuilder;
import org.nextprot.api.etl.statement.StatementETLBaseUnitTest;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.constants.AnnotationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.nextprot.api.annotation.builder.statement.dao.StatementDao;


@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({"unit", "unit-schema-nxflat", "build"})
@DirtiesContext
@ContextConfiguration("classpath:spring/core-context.xml")
public class StatementLoadAndRetrievedStatementsOnUnitDBTest extends StatementETLBaseUnitTest {
	
	@Autowired
	private StatementETLService statementETLService;

	@Autowired
	private StatementDao statementDao;

	
	@Test
	public void shouldExtractLoadAndRetriveStatements() {

		StatementExtractorService extractor = new StatementsExtractorLocalMockImpl();
		Set<Statement> rawStatements = extractor.getStatementsForSourceForGeneNameAndEnvironment(null, "2017-01-13", "msh6-variant-on-iso1-but-not-on-iso2");
		
		statementETLService.setStatementExtractorService(extractor);
		statementETLService.setStatementTransformerService(transformerMockedService);

		Set<Statement> mappedStatements = ((StatementETLServiceImpl) statementETLService).transformStatements(rawStatements, new ReportBuilder());
		
		 ((StatementETLServiceImpl) statementETLService).loadStatements(rawStatements, mappedStatements, true, new ReportBuilder());

		List<Statement> dbStatements = statementDao.findNormalStatements(AnnotationType.ENTRY, "NX_P52701");
		dbStatements.addAll(statementDao.findProteoformStatements(AnnotationType.ENTRY, "NX_P52701"));
		
		Assert.assertEquals(dbStatements.size(), mappedStatements.size());
		
	}


}
