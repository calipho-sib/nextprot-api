package com.nextprot.api.annotation.builder.statement.dao;

import java.util.List;

import org.nextprot.commons.statements.Statement;

public interface StatementDao {

	List<Statement> findNormalStatements(String entryName);

	List<Statement> findProteoformStatements(String entryName);

	List<Statement> findStatementsByAnnotIsoIds(List<String> ids);

	List<Statement> findStatementsByAnnotEntryId(String annotEntryId);

}
