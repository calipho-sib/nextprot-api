package org.nextprot.api.etl.statement.source;

import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.reader.StreamingJsonStatementReader;
import org.nextprot.commons.statements.specs.Specifications;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class SimpleStatementSource implements StatementSourceNew {

	private final Specifications specifications;
	private final URL url;
	private StreamingJsonStatementReader reader;

	public SimpleStatementSource(Specifications specifications, URL url) {

		this.specifications = specifications;
		this.url = url;
	}

	@Override
	public Specifications specifications() {

		return specifications;
	}

	private synchronized void lazyReaderCreation() throws IOException {

		if (reader == null) {
			reader = new StreamingJsonStatementReader(new InputStreamReader(url.openStream()),
					specifications, 1);
		}
	}

	@Override
	public Statement nextStatement() throws IOException {

		lazyReaderCreation();

		return reader.readOneStatement().orElse(null);
	}

	@Override
	public boolean hasStatement() throws IOException {

		lazyReaderCreation();

		return reader.hasNextStatement();
	}
}
