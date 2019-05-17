package org.nextprot.api.etl.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.etl.StatementSourceEnum;
import org.nextprot.api.etl.service.StatementLoaderService;
import org.nextprot.api.etl.service.StatementSourceService;
import org.nextprot.api.etl.service.StatementTransformerService;

import java.io.IOException;

public class MultipleBatchesStatementETLServiceTest {

	@InjectMocks
	private MultipleBatchesStatementETLService multipleBatchesStatementETLService = new MultipleBatchesStatementETLService();

	@Mock
	private MasterIdentifierService masterIdentifierService;

	@Mock
	private StatementSourceService statementSourceService;

	@Mock
	private StatementTransformerService statementTransformerService;

	@Mock
	private StatementLoaderService statementLoadService;

	@Before
	public void init() {

		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void extractTransformLoadStatements() throws IOException {

		String log = multipleBatchesStatementETLService.extractTransformLoadStatements(StatementSourceEnum.BioEditor, "X", true);

		System.out.println(log);
	}
}