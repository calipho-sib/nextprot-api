package org.nextprot.api.etl.statement.pipeline;


import java.io.IOException;


public abstract class PipedFilter extends Pipe {

	private final ThreadLocal<Boolean> endOfFlow;

	PipedFilter(int sectionWidth) {

		super(sectionWidth);
		endOfFlow = ThreadLocal.withInitial(() -> false);
	}

	@Override
	public void handleFlow() throws IOException {

		while (!endOfFlow.get()) {

			endOfFlow.set(filter(in, out));
		}
	}

	/**
	 * Filter statements coming from input port to output port
	 *
	 * @param in  input port
	 * @param out output port
	 * @return false if end of flow token has been received
	 * @throws IOException
	 */
	protected abstract boolean filter(PipedInputPort in, PipedOutputPort out) throws IOException;
}
