package org.nextprot.api.etl.service;

import org.nextprot.commons.statements.constants.NextProtSource;

public interface StatementETLService {
    
	String etlStatements(NextProtSource source, boolean load);

	void setStatementExtractorService(StatementExtractorService statementExtractorService);

	void setStatementTransformerService(StatementTransformerService statementTransformerService);

	void setStatementLoadService(StatementLoaderService statementLoadService);
	
}
