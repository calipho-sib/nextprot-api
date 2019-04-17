package org.nextprot.api.etl.service;

import org.nextprot.commons.statements.Statement;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SimpleStatementTransformerService {

	Optional<Statement> transformStatement(Statement rawStatement);

	List<Statement> transformVariantAndMutagenesisSet(Collection<Statement> subjects);
}
