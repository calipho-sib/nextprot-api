package org.nextprot.api.etl.service.impl;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.nextprot.api.core.dao.StatementDao;
import org.nextprot.api.etl.service.StatementETLService;
import org.nextprot.api.etl.statement.StatementETLBaseUnitTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

// TODO: Those tests should be rewritten
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({"dev", "build"})
@DirtiesContext
@ContextConfiguration("classpath:spring/core-context.xml")
public class StatementLoadAndRetrievedStatementsOnUnitDBTest extends StatementETLBaseUnitTest {

	@Autowired
	private StatementETLService statementETLService;

	@Autowired
	private StatementDao statementDao;

	/*
	@Test
	public void shouldExtractLoadAndRetrieveStatementsForBioEditor2() throws IOException {

		StatementExtractorService extractor = new StatementsExtractorLocalMockImpl();
		Collection<Statement> rawStatements = extractor.getStatementsFromJsonFile(StatementSource.BioEditor, "2017-01-13", "msh6-variant-on-iso1-but-not-on-iso2");

		Collection<Statement> mappedStatements =
				statementETLService.transformStatements(StatementSource.BioEditor, rawStatements, new ReportBuilder());

		statementETLService.loadStatements(StatementSource.BioEditor, rawStatements, mappedStatements, true, new ReportBuilder());

		Collection<Statement> loadedRS = statementLoaderService.getRsColl();
		Collection<Statement> loadedMS = statementLoaderService.getMsColl();

		Assert.assertEquals(rawStatements.size(), loadedRS.size());
		Assert.assertEquals(mappedStatements.size(), loadedMS.size());
	}

	@Test
	public void shouldExtractButNotLoad() throws IOException {

		StatementExtractorService extractor = new StatementsExtractorLocalMockImpl();
		Collection<Statement> rawStatements = extractor.getStatementsFromJsonFile(StatementSource.BioEditor, "2017-01-13", "msh6-variant-on-iso1-but-not-on-iso2");

		Collection<Statement> mappedStatements =
				statementETLService.transformStatements(StatementSource.BioEditor, rawStatements, new ReportBuilder());

		statementETLService.loadStatements(StatementSource.BioEditor, rawStatements, mappedStatements, false, new ReportBuilder());

		Collection<Statement> loadedRS = statementLoaderService.getRsColl();
		Collection<Statement> loadedMS = statementLoaderService.getMsColl();

		Assert.assertTrue(loadedRS.isEmpty());
		Assert.assertTrue(loadedMS.isEmpty());
	}

	@Test
	public void shouldExtractLoadAndRetrieveStatementsForGlyConnect() throws IOException {

		StatementExtractorService extractor = new StatementsExtractorLocalMockImpl();
			Collection<Statement> rawStatements = extractor.getStatementsFromJsonFile(StatementSource.GlyConnect, "2017-07-19", "few-entries");

		Collection<Statement> mappedStatements =
				statementETLService.transformStatements(StatementSource.GlyConnect, rawStatements, new ReportBuilder());

		statementETLService.loadStatements(StatementSource.BioEditor, rawStatements, mappedStatements, true, new ReportBuilder());

		Collection<Statement> loadedRS = statementLoaderService.getRsColl();
		Collection<Statement> loadedMS = statementLoaderService.getMsColl();

		Assert.assertEquals(rawStatements.size(), loadedRS.size());
		Assert.assertEquals(mappedStatements.size(), loadedMS.size());
	}*/
}
