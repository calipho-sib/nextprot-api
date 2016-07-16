package org.nextprot.api.etl.statement.service;

import java.util.List;

import org.nextprot.commons.statements.RawStatement;

public interface RawStatementRemoteService {

	List<RawStatement> getStatementsForSourceForGeneName(String source, String geneName);

	List<RawStatement> getStatementsForSource(String source);

}
