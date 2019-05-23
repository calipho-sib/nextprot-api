package org.nextprot.api.etl.statement.pipeline;

import org.nextprot.api.etl.statement.source.SimpleStatementSource;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadedPipeline {

	private final int NUMBER_THREADS = 1;

	private ExecutorService executor;

	public MultiThreadedPipeline() {

		executor = Executors.newFixedThreadPool(NUMBER_THREADS);
	}

	public void addTask(URL jsonURL) {

		//executor.submit(new PipelineTask(this, rdfTypeName));
	}

	//Task
	private static class PipelineTask implements Callable<Pipeline> {

		private SimpleStatementSource source;

		public PipelineTask(SimpleStatementSource source) {
			this.source = source;
		}

		@Override
		public Pipeline call() throws IOException {

			while(source.hasStatement()) {

				source.nextStatement();
			}
			return null;
		}
	}
}
