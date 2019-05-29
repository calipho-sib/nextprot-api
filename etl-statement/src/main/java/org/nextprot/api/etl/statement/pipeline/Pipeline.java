package org.nextprot.api.etl.statement.pipeline;

import org.nextprot.api.etl.statement.pipeline.pipes.PipedSource;
import org.nextprot.commons.statements.Statement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Pipeline {

	private PipedSource source;
	private List<Thread> threads;
	private final Monitorable monitorable;

	public Pipeline(DataCollector dataCollector) {

		source = dataCollector.getSource();
		monitorable = dataCollector.getMonitorable();
	}

	public void open() {

		threads = new ArrayList<>();

		source.openPipe(threads);
		monitorable.started(threads);
	}

	/**
	 * Wait for all threads in the pipe to terminate
	 */
	public void waitForThePipesToComplete() throws InterruptedException {

		for (Thread thread : threads) {
			thread.join();
			System.out.println("Pipe " + thread.getName() + ": closed");
		}
		monitorable.ended();
	}

	interface StartStep {

		default SourceStep start() {
			return start(new Deaf());
		}

		SourceStep start(Monitorable monitorable);
	}

	interface SourceStep {

		FilterStep source(Pump<Statement> pump);
	}

	interface FilterStep {

		FilterStep filter(Function<Integer, Filter> filterProvider) throws IOException;

		TerminateStep sink(Function<Integer, Sink> sinkProvider) throws IOException;
	}

	interface TerminateStep {

		Pipeline build() throws IOException;
	}

	interface Monitorable {

		void started(List<Thread> threads);

		void ended();
	}

	static class Deaf implements Monitorable {

		@Override
		public void started(List<Thread> threads) { }

		@Override
		public void ended() { }
	}

	static class DataCollector {

		private PipedSource source;
		private Monitorable monitorable;

		public PipedSource getSource() {
			return source;
		}

		public void setSource(PipedSource source) {
			this.source = source;
		}

		public Monitorable getMonitorable() {
			return monitorable;
		}

		public void setMonitorable(Monitorable monitorable) {
			this.monitorable = monitorable;
		}
	}
}
