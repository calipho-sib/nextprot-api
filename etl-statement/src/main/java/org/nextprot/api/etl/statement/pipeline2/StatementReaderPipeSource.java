package org.nextprot.api.etl.statement.pipeline2;

import org.nextprot.api.etl.statement.pipeline.Pump;
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
public class StatementReaderPipeSource extends Pipe {
	protected Pump<Statement> pump;  // The Reader we take data from

	/**
	 * To create a ReaderPipeSource, specify the Reader that data comes from
	 * and the Pipe sink that it should be sent to.
	 **/
	public StatementReaderPipeSource(Pump<Statement> pump, Pipe sink)
			throws IOException {
		super(sink);
		this.pump = pump;
	}

	/**
	 * This is the thread body.  When the pipe is started, this method copies
	 * statements from the Reader into the pipe
	 **/
	public void run() {
		try {
			List<Statement> collector = new ArrayList<>(50);
			int stmtsRead;
			while((stmtsRead = pump.pump(collector)) != -1)
				out.write(collector, 0, stmtsRead);
		}
		catch (IOException e) {}
		// When done with the data, close the Reader and the pipe
		finally { try { in.close(); out.close(); } catch (IOException e) {} }
	}

	/**
	 * This method overrides the getReader() method of Pipe.  Because this
	 * is a source thread, this method should never be called.  To make sure
	 * that it is never called, we throw an Error if it is.
	 **/
	protected PipedStatementReader getReader() {
		throw new Error("Can't connect to a ReaderPipeSource!");
	}
}
