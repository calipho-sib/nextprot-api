package org.nextprot.api.etl.service.impl;

import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.core.app.StatementSource;
import org.nextprot.commons.statements.Statement;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;


// NOTE: if those tests fails it is probably because http://kant.isb-sib.ch:9000 is down
// restart it by executing /share/sib/calipho/nxflat-proxy/statements-downloader/launch-web-statements-server.sh

// those tests depend on the fact that the web server on kant is alive ... if he is not the tests fail
@Ignore
public class StatementRemoteServiceTest {


	private static final String RELEASE = "2018-01-12";

	@Test
	public void shouldExtractRemoteStatementsForAGene() throws IOException {

		StatementRemoteServiceImpl srsi = new StatementRemoteServiceImpl();
		Collection<Statement> statements = srsi.getStatementsFromJsonFile(StatementSource.BioEditor, RELEASE, "AAK1@strauss");
		Assert.isTrue(!statements.isEmpty());
	}


	@Test
	public void shouldExtractRemoteStatementsForAllGenes() throws IOException {

		StatementRemoteServiceImpl srsi = new StatementRemoteServiceImpl();
		Collection<Statement> statements = srsi.getStatementsForSource(StatementSource.BioEditor, RELEASE);
		Assert.isTrue(!statements.isEmpty());
	}


	@Test
	public void shouldGetGeneNamesAndEnvironment() throws IOException {

		StatementRemoteServiceImpl srsi = new StatementRemoteServiceImpl();
		Set<String> geneNames = srsi.getJsonFilenamesForRelease(StatementSource.BioEditor, RELEASE);
		Assert.isTrue(geneNames.size() > 10);
	}


}
