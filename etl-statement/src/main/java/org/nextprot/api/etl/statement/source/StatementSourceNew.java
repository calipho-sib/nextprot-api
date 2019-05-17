package org.nextprot.api.etl.statement.source;


import org.nextprot.commons.statements.reader.StatementReader;
import org.nextprot.commons.statements.specs.Specifications;

import java.io.IOException;
import java.util.stream.Stream;

public interface StatementSourceNew<R extends StatementReader> {

	Specifications specifications();

	/** @return the next statement of null if no more statements */
	//Statement nextStatement() throws IOException;

	//boolean hasStatement();

	Stream<R> readers() throws IOException;
}
