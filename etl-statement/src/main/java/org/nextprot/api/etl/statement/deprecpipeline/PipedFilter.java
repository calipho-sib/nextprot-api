package org.nextprot.api.etl.statement.deprecpipeline;

public abstract class PipedFilter extends ConcurrentPipe {

	protected Pipe pipeIn;
	protected Pipe pipeOut;

	public PipedFilter(Pipe pipeIn, Pipe pipeOut) {
		this.pipeIn = pipeIn;
		this.pipeOut = pipeOut;
	}

	@Override
	public void run() {
		transform(pipeIn, pipeOut);
	}

	protected abstract void transform(Pipe input, Pipe output);

	public Pipe getPipeIn() {

		return pipeIn;
	}

	public Pipe getPipeOut() {

		return pipeOut;
	}
}
