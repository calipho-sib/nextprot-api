package org.nextprot.api.etl.statement.pipeline;

import org.nextprot.commons.statements.Statement;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class MockStatementPump implements Pump<Statement> {

	private final Iterator<Statement> iterator;

	public MockStatementPump(List<Statement> statements) {

		this.iterator = statements.iterator();
	}

	@Override
	public Statement pump() throws IOException {

		return iterator.next();
	}

	@Override
	public int capacity() {

		return 10;
	}

	@Override
	public int pump(List<Statement> collector) throws IOException {

		return 0;
	}

	@Override
	public boolean isEmpty() throws IOException {

		return iterator.hasNext();
	}

	@Override
	public void close() throws IOException {
	}
}
