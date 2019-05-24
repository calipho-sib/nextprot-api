package org.nextprot.api.etl.statement.pipeline2;

import org.nextprot.api.etl.statement.pipeline.PipedFilter;
import org.nextprot.api.etl.statement.pipeline.PipedSink;
import org.nextprot.api.etl.statement.pipeline.Pump;
import org.nextprot.commons.statements.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Pipeline {

	private StatementReaderPipeSource pipedSource;
	private List<PipeFilter> filters;
	private Pipe sink;

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

		private StatementReaderPipeSource pipedSource;
		private List<PipeFilter> filters = new ArrayList<>();
		private Pipe sink;

		public StatementReaderPipeSource getPipedSource() {
			return pipedSource;
		}

		public void setPipedSource(StatementReaderPipeSource pipedSource) {
			this.pipedSource = pipedSource;
		}

		public List<PipeFilter> getFilters() {
			return filters;
		}

		public void addFilter(PipeFilter filter) {
			this.filters.add(filter);
		}

		public Pipe getSink() {
			return sink;
		}

		public void setSink(NxFlatTableSink sink) {
			this.sink = sink;
		}
	}
}
