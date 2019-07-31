package org.nextprot.api.etl.pipeline.pump;

import com.google.common.base.Preconditions;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.reader.BufferableStatementReader;
import org.nextprot.commons.statements.reader.BufferedJsonStatementReader;
import org.nextprot.commons.statements.specs.Specifications;
import org.nextprot.commons.statements.specs.StatementSpecifications;
import org.nextprot.pipeline.statement.core.stage.source.Pump;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.function.BiFunction;


/**
 * A pump pumping statements from a URL source one by one
 */
public class HttpStatementPump implements Pump<Statement> {

	private final String url;
	private final StatementSpecifications specifications;
	private final BiFunction<String, StatementSpecifications, BufferableStatementReader> readerSupplier;

	// The creation of this reader is done once when either pump() or isSourceEmpty() is called
	private BufferableStatementReader reader;

	public HttpStatementPump(String url) throws PumpException {

		this(url, new Specifications.Builder().build(), HttpStatementPump::connectToJsonSource);
	}

	public HttpStatementPump(String url, StatementSpecifications specifications) throws PumpException {

		this(url, specifications, HttpStatementPump::connectToJsonSource);
	}

	public HttpStatementPump(String url, StatementSpecifications specifications,
	                         BiFunction<String, StatementSpecifications, BufferableStatementReader> readerSupplier) throws PumpException {

		Preconditions.checkNotNull(url);
		Preconditions.checkArgument(!url.isEmpty());

		this.url = url;
		this.specifications = specifications;
		this.readerSupplier = readerSupplier;
	}

	private static BufferableStatementReader connectToJsonSource(String url, StatementSpecifications specifications) throws PumpException {

		try {
			return new BufferedJsonStatementReader(new InputStreamReader(new URL(url).openStream()),
						specifications, 1);
		} catch (IOException e) {
			throw new PumpException("Could not create json reader: source="+url, e);
		}
	}

	private void buildReaderIfUndefined() {

		if (reader == null) {
			this.reader = readerSupplier.apply(url, specifications);
		}
	}

	public String getUrl() {
		return url;
	}

	@Override
	public Statement pump() throws PumpException {

		buildReaderIfUndefined();

		try {
			return reader.nextStatement();
		} catch (IOException e) {
			throw new PumpException("Could not pump statement: source="+url, e);
		}
	}

	@Override
	public boolean isSourceEmpty() throws PumpException {

		buildReaderIfUndefined();

		try {
			return !reader.hasStatement();
		} catch (IOException e) {
			throw new PumpException("Could test pump for emptiness: source="+url, e);
		}
	}

	@Override
	public void stop() throws PumpException {

		try {
			if (reader != null) {
				reader.close();
			}
		} catch (IOException e) {
			throw new PumpException("Could not stop the pump: source="+url, e);
		}
	}
}
