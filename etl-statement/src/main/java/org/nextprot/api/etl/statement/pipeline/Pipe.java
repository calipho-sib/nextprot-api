package org.nextprot.api.etl.statement.pipeline;

import org.nextprot.api.etl.statement.pipeline.ports.PipedInputPort;

import java.io.IOException;
import java.util.List;

public interface Pipe {

	void openPipe(List<Thread> collector);
	void closePipe() throws IOException;
	void connect(Pipe receiver) throws IOException;
	PipedInputPort getInputPort();
	int getSectionWidth();
}
