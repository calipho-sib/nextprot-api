package org.nextprot.api.etl.service.transform;

import org.nextprot.api.etl.service.impl.StatementETLServiceImpl.ReportBuilder;
import org.nextprot.commons.statements.Statement;

import java.util.Collection;

public interface StatementTransformerService {

	Collection<Statement> transformStatements(Collection<Statement> rawStatements, ReportBuilder report);
}
