package org.nextprot.api.etl.service;

import org.nextprot.api.etl.NextProtSource;
import org.nextprot.commons.statements.Statement;

import java.sql.SQLException;
import java.util.Collection;

public interface StatementLoaderService {

	void loadRawStatementsForSource(Collection<Statement> statements, NextProtSource source) throws SQLException;
	void loadStatementsMappedToEntrySpecAnnotationsForSource(Collection<Statement> statements, NextProtSource source)  throws SQLException;
	
}