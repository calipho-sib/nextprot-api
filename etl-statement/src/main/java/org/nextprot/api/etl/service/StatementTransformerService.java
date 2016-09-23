package org.nextprot.api.etl.service;

import java.util.Set;

import org.nextprot.api.etl.service.impl.StatementETLServiceImpl.ReportBuilder;
import org.nextprot.commons.statements.Statement;

public interface StatementTransformerService {

	Set<Statement> transformStatements(Set<Statement> rawStatements, ReportBuilder report);

}
