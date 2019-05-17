package org.nextprot.api.etl.statement.source;


import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.specs.Specifications;

import java.io.IOException;

public interface StatementSource {

	Specifications specifications();

	/** @return the next statement of null if no more statements */
	Statement nextStatement() throws IOException;

	/** @return true if has more statements */
	boolean hasStatement() throws IOException;
}