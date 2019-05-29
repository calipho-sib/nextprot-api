package org.nextprot.api.etl.statement.pipeline.pipes;

import org.nextprot.api.etl.statement.pipeline.Pump;
import org.nextprot.api.etl.statement.pipeline.ports.PipedInputPort;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.reader.BufferableStatementReader;
import org.nextprot.commons.statements.reader.BufferedJsonStatementReader;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is a source of data for a pipe of threads.
 * It pumps statements and send them in a connected receiver
 * but cannot serve as a receiver for any other Pipe: it must always be at the beginning,
 * or "source" of the pipe.
 **/
public class PipedSource extends ConcurrentPipe {

	protected Pump<Statement> pump;

	public PipedSource(Pump<Statement> pump) {

		super(pump.capacity());
		this.pump = pump;
	}

	@Override
	public void handleFlow() throws IOException {

		List<Statement> collector = new ArrayList<>();
		int stmtsRead;

		while((stmtsRead = pump.pump(collector)) != -1) {
			System.out.println(Thread.currentThread().getName()
					+ ": about to spill "+ stmtsRead + " statements...");

			out.write(collector, 0, stmtsRead);

			collector.clear();
		}

		out.write(END_OF_FLOW_TOKEN);
	}

	@Override
	public String getName() {
		return "Source";
	}

	public Pump<Statement> getPump() {
		return pump;
	}

	@Override
	public void closePipe() throws IOException {

		System.out.println("Pump: stopped");
		pump.close();
		super.closePipe();
	}

	/**
	 * This method overrides the getReader() method of Pipe.  Because this
	 * is a source thread, this method should never be called.  To make sure
	 * that it is never called, we throw an Error if it is.
	 **/
	public PipedInputPort getInputPort() {

		throw new Error("It is a Source, can't connect to a PipedInputPort!");
	}

	public static class StatementPump implements Pump<Statement> {

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
}
