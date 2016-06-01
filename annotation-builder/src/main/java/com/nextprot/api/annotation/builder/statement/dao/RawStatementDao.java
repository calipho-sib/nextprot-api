package com.nextprot.api.annotation.builder.statement.dao;

import java.util.List;

import org.nextprot.commons.statements.RawStatement;

public interface RawStatementDao {
	
	List<RawStatement> findRawStatements();

}
