package org.nextprot.api.etl.service;

import java.util.Set;

import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.constants.NextProtSource;

public interface StatementExtractorService {

	Set<Statement> getStatementsForSourceForGeneNameAndEnvironment(NextProtSource source, String release, String geneNameAndEnvironment);

	Set<Statement> getStatementsForSource(NextProtSource source, String release);


}
