package org.nextprot.api.etl.statement.pipeline;

public abstract class Sink extends Pipe {

	public Sink(int readerCapacity) {

		super(new PipedInputPort(readerCapacity));
	}

	@Override
	public String getName() {

		return "Sink";
	}
}
