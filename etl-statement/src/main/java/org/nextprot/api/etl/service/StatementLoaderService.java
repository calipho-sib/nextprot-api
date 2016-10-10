package org.nextprot.api.etl.service;

import java.sql.SQLException;
import java.util.Set;

import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.constants.NextProtSource;

public interface StatementLoaderService {

	void loadRawStatementsForSource(Set<Statement> statements, NextProtSource source) throws SQLException;
	void loadStatementsMappedToEntrySpecAnnotationsForSource(Set<Statement> statements, NextProtSource source)  throws SQLException;
	
}