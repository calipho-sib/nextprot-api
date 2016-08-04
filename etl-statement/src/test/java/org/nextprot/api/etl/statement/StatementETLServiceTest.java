package org.nextprot.api.etl.statement;

import org.junit.Test;
import org.nextprot.api.etl.service.StatementETLService;
import org.nextprot.commons.statements.constants.NextProtSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({ "dev" })
public class StatementETLServiceTest extends StatementETLServiceBaseTest {

	@Autowired
	private StatementETLService statementSourceCollectorAndLoaderService;

	@Test
	public void shouldETLBioeditorStatements() throws Exception {

		statementSourceCollectorAndLoaderService.etlStatements(NextProtSource.BioEditor);
	}

}