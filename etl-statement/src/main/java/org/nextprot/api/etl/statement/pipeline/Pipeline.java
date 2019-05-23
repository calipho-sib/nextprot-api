package org.nextprot.api.etl.statement.pipeline;

import org.nextprot.commons.statements.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Pipeline {

	private PipedSource pipedSource;
	private List<PipedFilter> filters;
	private PipedSink sink;

	public Pipeline(DataCollector dataCollector) {

		pipedSource = dataCollector.getPipedSource();
		filters = dataCollector.getFilters();
		sink = dataCollector.getSink();
	}

	public void start() {

		pipedSource.startPipe();
		filters.forEach(f -> f.startPipe());
		sink.startPipe();
	}

	public interface Builder {

		interface Start {

			Builder.Source start();
		}

		interface Source {

			Builder.Filter source(Pump<Statement> pump);
		}

		interface Filter {

			Builder.Filter filter(BiFunction<Pipe, Pipe, PipedFilter> filterProvider);

			Builder.Terminate sink(Function<Pipe, PipedSink> sinkProvider);
		}

		interface Terminate {

			Pipeline build();
		}
	}

	public static class DataCollector {

		private PipedSource pipedSource;
		private List<PipedFilter> filters = new ArrayList<>();
		private PipedSink sink;

		public PipedSource getPipedSource() {
			return pipedSource;
		}

		public void setPipedSource(PipedSource pipedSource) {
			this.pipedSource = pipedSource;
		}

		public List<PipedFilter> getFilters() {
			return filters;
		}

		public void addFilter(PipedFilter filter) {
			this.filters.add(filter);
		}

		public PipedSink getSink() {
			return sink;
		}

		public void setSink(PipedSink sink) {
			this.sink = sink;
		}
	}
}
