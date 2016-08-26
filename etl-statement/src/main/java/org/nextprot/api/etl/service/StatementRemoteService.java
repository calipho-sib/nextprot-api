package org.nextprot.api.etl.service;

import java.util.List;

import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.constants.NextProtSource;

public interface StatementRemoteService {

	List<Statement> getStatementsForSourceForGeneName(NextProtSource source, String geneName);

	List<Statement> getStatementsForSource(NextProtSource source);

}
