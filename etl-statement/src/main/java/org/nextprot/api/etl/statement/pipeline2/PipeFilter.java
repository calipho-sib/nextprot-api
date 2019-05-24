package org.nextprot.api.etl.statement.pipeline2;


import java.io.IOException;

/**
 * This abstract class simplifies (somewhat) the task of writing a
 * filter pipe--i.e. one that reads data from one Pipe thread, filters
 * it somehow, and writes the results to some other Pipe.
 **/
public abstract class PipeFilter extends Pipe {

	public PipeFilter(Pipe sink) throws IOException {
		super(sink, new PipedStatementReader(1));
	}

	@Override
	public void run() {

		try {
			filter(in, out);
		} catch (IOException e) {
			System.err.println(e.getMessage() + " in thread " + Thread.currentThread().getName());
		}
		finally {
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				System.err.println(e.getMessage() + " in thread " + Thread.currentThread().getName());
			}
		}
	}

	/** The method that subclasses must implement to do the filtering */
	abstract public void filter(PipedStatementReader in, PipedStatementWriter out) throws IOException;
}
