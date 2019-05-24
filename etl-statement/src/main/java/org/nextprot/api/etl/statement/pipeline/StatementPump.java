package org.nextprot.api.etl.statement.pipeline;

import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.reader.BufferableStatementReader;
import org.nextprot.commons.statements.reader.BufferedJsonStatementReader;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class StatementPump implements Pump<Statement> {

	private final BufferableStatementReader reader;
	private final int capacity;

	public StatementPump(Reader reader) throws IOException {

		this(reader, 100);
	}

	public StatementPump(Reader reader, int capacity) throws IOException {

		this.reader = new BufferedJsonStatementReader(reader, capacity);
		this.capacity = capacity;
	}

	@Override
	public Statement pump() throws IOException {

		return reader.nextStatement();
	}

	@Override
	public int capacity() {

		return capacity;
	}

	@Override
	public int pump(List<Statement> collector) throws IOException {

		return reader.readStatements(collector);
	}

	@Override
	public boolean isEmpty() throws IOException {

		return reader.hasStatement();
	}

	@Override
	public void close() throws IOException {

		reader.close();
	}
}
