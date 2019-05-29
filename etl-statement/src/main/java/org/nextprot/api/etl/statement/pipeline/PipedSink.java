package org.nextprot.api.etl.statement.pipeline;

public abstract class PipedSink extends Pipe {

	protected PipedSink(int crossSection) {

		super(crossSection);
	}

	@Override
	public void connect(Pipe receiver) {

		throw new Error("It is a sink, can't connect to a PipedOutputPort!");
	}
}
