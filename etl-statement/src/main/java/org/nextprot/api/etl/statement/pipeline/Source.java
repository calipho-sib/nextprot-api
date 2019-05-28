package org.nextprot.api.etl.statement.pipeline;

import org.nextprot.commons.statements.Statement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is a source of data for a pipe of threads.  It connects to
 * a sink, but cannot serve as a sink for any other Pipe.  That is, it must
 * always be at the beginning, or "source" of the pipe.  For this class,
 * the source of data is the specified Reader object (such as a FileReader).
 **/
public class Source extends Pipe {

	protected Pump<Statement> pump;

	/**
	 * To create a ReaderPipeSource, specify the Reader that data comes from
	 * and the Pipe sink that it should be sent to.
	 **/
	public Source(Pump<Statement> pump) {
		super(null);
		this.pump = pump;
	}

	/**
	 * This is the thread body.  When the pipe is started, this method copies
	 * statements from the Reader into the pipe
	 **/
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
