package org.nextprot.api.etl.service;


import org.nextprot.api.etl.NextProtSource;
import org.nextprot.api.etl.service.impl.StatementETLServiceImpl;
import org.nextprot.commons.statements.Statement;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

public interface StatementETLService {

	void setStatementLoadService(StatementLoaderService statementLoadService);

	String etlStatements(NextProtSource source, String release, boolean load) throws IOException;

	Set<Statement> extractStatements(NextProtSource source, String release, StatementETLServiceImpl.ReportBuilder report) throws IOException;

	Collection<Statement> transformStatements(NextProtSource source, Collection<Statement> rawStatements, StatementETLServiceImpl.ReportBuilder report);

	void loadStatements(NextProtSource source, Collection<Statement> rawStatements, Collection<Statement> mappedStatements, boolean load, StatementETLServiceImpl.ReportBuilder report);
}
