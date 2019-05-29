package org.nextprot.api.etl.statement.pipeline.pipes;

import org.nextprot.api.etl.statement.pipeline.Pipe;
import org.nextprot.api.etl.statement.pipeline.Sink;

public abstract class PipedSink extends ConcurrentPipe implements Sink {

	protected PipedSink(int sectionWidth) {

		super(sectionWidth);
	}

	@Override
	public void connect(Pipe receiver) {

		throw new Error("It is a sink, can't connect to a PipedOutputPort!");
	}
}
