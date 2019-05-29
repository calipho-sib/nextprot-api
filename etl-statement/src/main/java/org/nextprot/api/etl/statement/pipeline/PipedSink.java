package org.nextprot.api.etl.statement.pipeline;

public abstract class PipedSink extends Pipe {

	protected PipedSink(int crossSection) {

		super(crossSection);
	}

	@Override
	public String getName() {

		return "Sink";
	}
}
