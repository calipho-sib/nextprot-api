package org.nextprot.api.etl.statement.pipeline;

import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.reader.BufferableStatementReader;

import java.io.IOException;
import java.util.List;

public class StatementPump implements Pump<Statement> {

	private final BufferableStatementReader reader;

	public StatementPump(BufferableStatementReader reader) {

		this.reader = reader;
	}

	@Override
	public Statement pump() throws IOException {

		return reader.nextStatement();
	}

	@Override
	public int pump(List<Statement> collector) throws IOException {

		return reader.readStatements(collector);
	}

	@Override
	public boolean isEmpty() throws IOException {

		return reader.hasStatement();
	}
}
