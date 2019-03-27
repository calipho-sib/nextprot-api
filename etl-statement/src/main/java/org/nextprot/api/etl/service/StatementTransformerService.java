package org.nextprot.api.etl.service;

import org.nextprot.api.etl.NextProtSource;
import org.nextprot.api.etl.service.impl.StatementETLServiceImpl.ReportBuilder;
import org.nextprot.commons.statements.Statement;

import java.util.Collection;

public interface StatementTransformerService {

	Collection<Statement> transformStatements(NextProtSource nextProtSource, Collection<Statement> rawStatements, ReportBuilder report);
}
