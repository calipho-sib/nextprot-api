package org.nextprot.api.etl.statement.pipeline;


import java.io.IOException;


public abstract class PipedFilter extends Pipe {

	private final ThreadLocal<Boolean> endOfFlow;

	PipedFilter(int crossSection) {

		super(crossSection);
		endOfFlow = ThreadLocal.withInitial(() -> false);
	}

	@Override
	public void run() {

		try {
			while(!endOfFlow.get()) {

				endOfFlow.set(filter(in, out));
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
