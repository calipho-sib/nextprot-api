package org.nextprot.api.etl.statement.pipeline;


import java.io.IOException;


public abstract class Filter extends Pipe {

	Filter(int capacity) {
		super(new PipedInputPort(capacity));
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

	abstract public void filter(PipedInputPort in, PipedOutputPort out) throws IOException;
}
