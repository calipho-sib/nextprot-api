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
	public void run() {

		try {
			List<Statement> collector = new ArrayList<>();
			int stmtsRead;
			while((stmtsRead = pump.pump(collector)) != -1) {
				System.out.println(Thread.currentThread().getName()
						+ ": about to spill "+ stmtsRead + " statements...");

				out.write(collector, 0, stmtsRead);

				collector.clear();
			}
			// sending end of flow token
			System.out.println("end of flow");
			out.write(null);
		}
		catch (IOException e) {
			System.err.println(e.getMessage() + " in thread " + Thread.currentThread().getName());
		}
		// When done with the data, close the Reader and the pipe
		finally {
			try {
				pump.close();
				out.close();
			}
			catch (IOException e) {
				System.err.println(e.getMessage() + " in thread " + Thread.currentThread().getName());
			}
		}
	}

	/**
	 * This method overrides the getReader() method of Pipe.  Because this
	 * is a source thread, this method should never be called.  To make sure
	 * that it is never called, we throw an Error if it is.
	 **/
	protected PipedInputPort getInputPort() {
		throw new Error("Can't connect to a PipedInputPort!");
	}

	@Override
	public String getName() {
		return "Source";
	}
}
