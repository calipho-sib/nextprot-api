package org.nextprot.api.etl.statement.pipeline.pipes;


import org.nextprot.api.etl.statement.pipeline.Filter;

import java.io.IOException;


public abstract class PipedFilter extends ConcurrentPipe implements Filter {

	private final ThreadLocal<Boolean> endOfFlow;

	protected PipedFilter(int sectionWidth) {

		super(sectionWidth);
		endOfFlow = ThreadLocal.withInitial(() -> false);
	}

	@Override
	public void handleFlow() throws IOException {

		while (!endOfFlow.get()) {

			endOfFlow.set(filter(in, out));
		}
	}
}
