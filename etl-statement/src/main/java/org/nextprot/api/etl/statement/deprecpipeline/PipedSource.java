package org.nextprot.api.etl.statement.deprecpipeline;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.etl.statement.pipeline.Pump;
import org.nextprot.commons.statements.Statement;

import java.io.IOException;

/**
 * A source pumps statements from a Pump and spill them to a pipe.
 * It should be the first element of a pipeline.
 */
public class PipedSource extends ConcurrentPipe {

	private final Pump<Statement> pump;
	private final Pipe pipeOut;

	public PipedSource(Pump<Statement> pump, Pipe pipeOut) {

		this.pump = pump;
		this.pipeOut = pipeOut;
	}

	@Override
	public void run() {

		try {
			while (!pump.isEmpty()) {
				pipeOut.spill(pump.pump());
			}
		} catch (IOException e) {
			throw new NextProtException(e.getMessage());
		}
	}

	public Pipe getPipe() {

		return pipeOut;
	}
}