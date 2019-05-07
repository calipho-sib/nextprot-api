package org.nextprot.api.etl.service;


import org.nextprot.api.etl.NextProtSource;
import org.nextprot.api.etl.service.impl.StatementETLServiceImpl;
import org.nextprot.commons.statements.Statement;

import java.io.IOException;
import java.util.Collection;

public interface BatchedStatementETLService {

	String etlStatements(NextProtSource source, String release, boolean load) throws IOException;

	/**
	 * Build mapped statements from the raw statements mainly by calculating mapping fields
	 *
	 * @param rawStatements the original statements coming from a data source
	 * @return new mapped statements
	 */
	Collection<Statement> buildMappedStatements(Collection<Statement> rawStatements, StatementETLServiceImpl.ReportBuilder report);

	void loadStatements(NextProtSource source, Collection<Statement> rawStatements, Collection<Statement> mappedStatements, StatementETLServiceImpl.ReportBuilder report);
}
