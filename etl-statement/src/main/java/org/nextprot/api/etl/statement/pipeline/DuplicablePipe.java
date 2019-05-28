package org.nextprot.api.etl.statement.pipeline;

public interface DuplicablePipe {

	DuplicablePipe duplicate();
	PipedInputPort getPipedInputPort();
	PipedOutputPort getPipedOutputPort();
}
