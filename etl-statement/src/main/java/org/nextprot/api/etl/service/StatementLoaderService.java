package org.nextprot.api.etl.service;

import org.nextprot.api.core.app.StatementSource;
import org.nextprot.commons.statements.Statement;

import java.sql.SQLException;
import java.util.Collection;

public interface StatementLoaderService {

	void loadRawStatementsForSource(Collection<Statement> statements, StatementSource source) throws SQLException;
	void loadEntryMappedStatementsForSource(Collection<Statement> statements, StatementSource source)  throws SQLException;
	void deleteRawStatements(StatementSource source) ;
	void deleteEntryMappedStatements(StatementSource source);
} 