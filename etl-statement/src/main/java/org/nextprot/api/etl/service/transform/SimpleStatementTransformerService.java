package org.nextprot.api.etl.service.transform;

import org.nextprot.commons.statements.Statement;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SimpleStatementTransformerService {

	// FIXME: redundant with transformSubject ?
	Optional<Statement> transformStatement(Statement rawStatement);

	// FIXME: redundant with transformStatement ?
	Statement transformSubject(Statement rawStatement);

	List<Statement> transformSubjects(Collection<Statement> subjects);
}
