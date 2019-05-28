package org.nextprot.api.etl.statement.pipeline;

import org.nextprot.commons.statements.Statement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Pipeline {

	private PipedSource source;
	private List<Thread> threads;

	public Pipeline(DataCollector dataCollector) throws IOException {

		source = dataCollector.getSource();
	}

	public void open() {

		threads = new ArrayList<>();

		source.openPipe(threads);
	}

	/** Wait for all threads in the pipe to terminate */
	public void waitForThePipesToComplete() throws InterruptedException {

		for (Thread thread : threads) {
			thread.join();
			System.out.println("Pipe "+thread.getName()+": completed");
		}
	}

	public interface Builder {

		interface Start {

			Builder.Source start();
		}

		interface Source {

			Filter source(Pump<Statement> pump);
		}

		interface Filter {

			Filter filter(Function<Integer, PipedFilter> filterProvider) throws IOException;

			Builder.Terminate sink(Function<Integer, PipedSink> sinkProvider) throws IOException;
		}

		interface Terminate {

			Pipeline build() throws IOException;
		}
	}

	public static class DataCollector {

		private PipedSource source;

		public PipedSource getSource() {
			return source;
		}

		public void setSource(PipedSource source) {
			this.source = source;
		}
	}
}
