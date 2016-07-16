package com.nextprot.api.annotation.builder.statement.dao;

import java.util.List;

import org.nextprot.commons.statements.RawStatement;

public interface RawStatementDao {

	List<RawStatement> findNormalRawStatements(String entryName);

	List<RawStatement> findPhenotypeRawStatements(String entryName);

	List<RawStatement> findRawStatementsByAnnotIsoId(String annotIsoId);
	
	List<RawStatement> findRawStatementsByAnnotEntryId(String annotEntryId);
	
}
