package org.nextprot.api.etl.service.impl;

import org.junit.Test;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.constants.NextProtSource;
import org.springframework.util.Assert;

import java.util.Set;


public class StatementRemoteServiceTest {


	private static final String RELEASE = "2018-01-12";

	@Test
	public void shouldExtractRemoteStatementsForAGene() {

		StatementRemoteServiceImpl srsi = new StatementRemoteServiceImpl();
		Set<Statement> statements = srsi.getStatementsFromJsonFile(NextProtSource.BioEditor, RELEASE, "AAK1@strauss");
		Assert.isTrue(!statements.isEmpty());

	}


	@Test
	public void shouldExtractRemoteStatementsForAllGenes() {

		StatementRemoteServiceImpl srsi = new StatementRemoteServiceImpl();
		Set<Statement> statements = srsi.getStatementsForSource(NextProtSource.BioEditor, RELEASE);
		Assert.isTrue(!statements.isEmpty());

	}


	@Test
	public void shouldGetGeneNamesAndEnvironment() {

		StatementRemoteServiceImpl srsi = new StatementRemoteServiceImpl();
		Set<String> geneNames = srsi.getJsonFilenamesForRelease(NextProtSource.BioEditor, RELEASE);
		Assert.isTrue(geneNames.size() > 10);

	}


}
