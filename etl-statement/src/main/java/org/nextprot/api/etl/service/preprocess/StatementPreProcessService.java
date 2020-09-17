package org.nextprot.api.etl.service.preprocess;

import org.nextprot.api.core.app.StatementSource;
import org.nextprot.commons.statements.Statement;

import java.util.Collection;
import java.util.Set;

/**
 * Raw statement pre process service interface
 */
public interface StatementPreProcessService {

    /**
     * Processes the respective pre-process logic
     * @param statements
     * @return Pre-processed statements
     */
    Set<Statement> process(StatementSource source, Collection<Statement> statements);
}
