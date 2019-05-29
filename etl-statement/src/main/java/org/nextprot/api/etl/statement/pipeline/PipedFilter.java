package org.nextprot.api.etl.statement.pipeline;


import java.io.IOException;


public abstract class PipedFilter extends Pipe {

	PipedFilter(int crossSection) {

		super(crossSection);
	}

	@Override
	public void run() {

		try {
			boolean endOfFlow = false;

			while(!endOfFlow) {

				endOfFlow = filter(in, out);
			}
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

	/**
	 * Filter statements coming from input port to output port
	 * @param in input port
	 * @param out output port
	 * @return false if end of flow token has been received
	 * @throws IOException
	 */
	abstract public boolean filter(PipedInputPort in, PipedOutputPort out) throws IOException;
}
