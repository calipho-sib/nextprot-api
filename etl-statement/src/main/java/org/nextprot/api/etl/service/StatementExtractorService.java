package org.nextprot.api.etl.service;

import org.nextprot.api.etl.StatementSource;
import org.nextprot.commons.statements.Statement;

import java.io.IOException;
import java.util.Collection;

public interface StatementExtractorService {

	Collection<Statement> getStatementsFromJsonFile(StatementSource source, String release, String jsonFileName) throws IOException;
	Collection<Statement> getStatementsForSource(StatementSource source, String release) throws IOException;
}
