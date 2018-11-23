package org.nextprot.api.etl.service;

import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.constants.NextProtSource;

import java.io.IOException;
import java.util.Set;

public interface StatementExtractorService {

	Set<Statement> getStatementsFromJsonFile(NextProtSource source, String release, String jsonFileName) throws IOException;
	Set<Statement> getStatementsForSource(NextProtSource source, String release) throws IOException;
}
