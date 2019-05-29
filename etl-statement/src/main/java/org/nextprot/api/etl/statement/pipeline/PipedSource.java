package org.nextprot.api.etl.statement.pipeline;

import org.nextprot.commons.statements.Statement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is a source of data for a pipe of threads.
 * It pumps statements and send them in a connected receiver
 * but cannot serve as a receiver for any other Pipe: it must always be at the beginning,
 * or "source" of the pipe.
 **/
public class PipedSource extends Pipe {

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

	protected void closePipe() throws IOException {

		pump.close();
		super.closePipe();
	}

	/**
	 * This method overrides the getReader() method of Pipe.  Because this
	 * is a source thread, this method should never be called.  To make sure
	 * that it is never called, we throw an Error if it is.
	 **/
	protected PipedInputPort getInputPort() {

		throw new Error("It is a Source, can't connect to a PipedInputPort!");
	}
}
