package org.nextprot.api.etl.service;

import java.util.Set;

import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.constants.NextProtSource;

public interface StatementExtractorService {

	Set<Statement> getStatementsForSourceForGeneName(NextProtSource source, String geneName);

	Set<Statement> getStatementsForSource(NextProtSource source);


}
