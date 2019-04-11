package org.nextprot.api.etl.service;

import org.nextprot.api.etl.NextProtSource;
import org.nextprot.commons.statements.Statement;

import java.sql.SQLException;
import java.util.Set;

public interface StatementLoaderService {

	void loadRawStatementsForSource(Set<Statement> statements, NextProtSource source) throws SQLException;
	void loadStatementsMappedToEntrySpecAnnotationsForSource(Set<Statement> statements, NextProtSource source)  throws SQLException;
	
}