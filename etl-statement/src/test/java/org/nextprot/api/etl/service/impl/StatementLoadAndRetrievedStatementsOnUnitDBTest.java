package org.nextprot.api.etl.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nextprot.api.etl.StatementSource;
import org.nextprot.api.etl.service.StatementExtractorService;
import org.nextprot.api.etl.service.StatementLoaderService;
import org.nextprot.api.etl.service.impl.SingleBatchStatementETLService.ReportBuilder;
import org.nextprot.commons.statements.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;


@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({"dev", "build"})
@DirtiesContext
@ContextConfiguration("classpath:spring/core-context.xml")
public class StatementLoadAndRetrievedStatementsOnUnitDBTest {

	@Autowired
	private SingleBatchStatementETLService statementETLService;

	private StatementLoaderServiceMocked statementLoaderService;

	@Before
	public void setup() {
		statementLoaderService = new StatementLoaderServiceMocked();
		statementETLService.setStatementLoadService(statementLoaderService);
	}

	private static class StatementLoaderServiceMocked implements StatementLoaderService {

		private Collection<Statement> rsColl = new ArrayList<>();
		private Collection<Statement> msColl = new ArrayList<>();

		@Override
		public void loadRawStatementsForSource(Collection<Statement> statements, StatementSource source) {
			rsColl.addAll(statements);
		}

		@Override
		public void loadStatementsMappedToEntrySpecAnnotationsForSource(Collection<Statement> statements, StatementSource source) {
			msColl.addAll(statements);
		}

		public Collection<Statement> getRsColl() {
			return rsColl;
		}

		public Collection<Statement> getMsColl() {
			return msColl;
		}
	}

	@Test
	public void shouldExtractLoadAndRetrieveStatementsForBioEditor() throws IOException {

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
	}
}
