package org.nextprot.api.etl.service;

import org.nextprot.api.etl.StatementSourceEnum;
import org.nextprot.commons.statements.Statement;

import java.io.IOException;
import java.util.Collection;

public interface StatementExtractorService {

	Collection<Statement> getStatementsFromJsonFile(StatementSourceEnum source, String release, String jsonFileName) throws IOException;
	Collection<Statement> getStatementsForSource(StatementSourceEnum source, String release) throws IOException;
}
