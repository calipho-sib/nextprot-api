package org.nextprot.api.etl.statement.pipeline;

public abstract class PipedSink extends ConcurrentPipe {

	protected Pipe pipeIn;

	public PipedSink(Pipe pipeIn) {
		this.pipeIn = pipeIn;
	}

	@Override
	public void run() {
		takeFrom(pipeIn);
	}

	public abstract void takeFrom(Pipe pipe);

	public Pipe getPipeIn() {

		return pipeIn;
	}
}