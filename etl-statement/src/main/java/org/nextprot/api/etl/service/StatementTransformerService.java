package org.nextprot.api.etl.service;

import org.nextprot.api.etl.service.impl.StatementETLServiceImpl.ReportBuilder;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.constants.NextProtSource;

import java.util.Set;

public interface StatementTransformerService {

	Set<Statement> transformStatements(NextProtSource nextProtSource, Set<Statement> rawStatements, ReportBuilder report);

}
