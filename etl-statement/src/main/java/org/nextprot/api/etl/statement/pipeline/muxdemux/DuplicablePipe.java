package org.nextprot.api.etl.statement.pipeline.muxdemux;

import org.nextprot.api.etl.statement.pipeline.ports.PipedInputPort;
import org.nextprot.api.etl.statement.pipeline.ports.PipedOutputPort;

public interface DuplicablePipe {

	DuplicablePipe duplicate();
	PipedInputPort getPipedInputPort();
	PipedOutputPort getPipedOutputPort();
}
