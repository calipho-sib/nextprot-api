package org.nextprot.api.etl.service;

import org.nextprot.api.etl.NextProtSource;
import org.nextprot.commons.statements.Statement;

import java.io.IOException;
import java.util.Collection;

public interface StatementExtractorService {

	Collection<Statement> getStatementsFromJsonFile(NextProtSource source, String release, String jsonFileName) throws IOException;
	Collection<Statement> getStatementsForSource(NextProtSource source, String release) throws IOException;
}
