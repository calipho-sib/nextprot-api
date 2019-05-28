package org.nextprot.api.etl.statement.pipeline;

public abstract class PipedSink extends Pipe {

	public PipedSink(int readerCapacity) {

		super(new PipedInputPort(readerCapacity));
	}

	@Override
	public String getName() {

		return "Sink";
	}
}
