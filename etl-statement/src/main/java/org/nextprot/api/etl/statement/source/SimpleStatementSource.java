package org.nextprot.api.etl.statement.source;

import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.reader.BufferableStatementReader;
import org.nextprot.commons.statements.reader.BufferedJsonStatementReader;
import org.nextprot.commons.statements.specs.StatementSpecifications;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

public class SimpleStatementSource implements BufferableStatementReader {

	private final StatementSpecifications specifications;
	private final URL url;
	private final int bufferSize;
	private BufferableStatementReader reader;

	public SimpleStatementSource(StatementSpecifications specifications, URL url) {

		this(specifications, url, 1);
	}

	public SimpleStatementSource(StatementSpecifications specifications, URL url, int bufferSize) {

		this.specifications = specifications;
		this.url = url;
		this.bufferSize = bufferSize;
	}

	@Override
	public StatementSpecifications getSpecifications() {

		return specifications;
	}

	private synchronized void lazyReaderCreation() throws IOException {

		if (reader == null) {
			reader = new BufferedJsonStatementReader(new InputStreamReader(url.openStream()),
					specifications, bufferSize);
		}
	}

	@Override
	public Statement nextStatement() throws IOException {

		lazyReaderCreation();

		return reader.nextStatement();
	}

	@Override
	public boolean hasStatement() throws IOException {

		lazyReaderCreation();

		return reader.hasStatement();
	}

	@Override
	public List<Statement> readStatements() throws IOException {

		return reader.readStatements();
	}

	@Override
	public int readStatements(List<Statement> buffer) throws IOException {

		return reader.readStatements(buffer);
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}
}
