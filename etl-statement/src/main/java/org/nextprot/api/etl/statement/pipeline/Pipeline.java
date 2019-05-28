package org.nextprot.api.etl.statement.pipeline;

import org.nextprot.commons.statements.Statement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Pipeline {

	private Source source;
	private List<Filter> filters;
	private Sink sink;

	public Pipeline(DataCollector dataCollector) throws IOException {

		source = dataCollector.getSource();

		Pipe src = source;
		filters = dataCollector.getFilters();

		for (Filter filter : filters) {

			src.connect(filter);
			src = filter;
		}

		sink = dataCollector.getSink();
		src.connect(sink);
	}

	public void start() {

		source.openPipe();
	}

	public interface Builder {

		interface Start {

			Builder.Source start();
		}

		interface Source {

			Filter source(Pump<Statement> pump);
		}

		interface Filter {

			Filter filter(BiFunction<Pipe, Pipe, org.nextprot.api.etl.statement.pipeline.Filter> filterProvider);

			Builder.Terminate sink(Function<Pipe, Sink> sinkProvider);
		}

		interface Terminate {

			Pipeline build();
		}
	}

	public static class DataCollector {

		private Source source;
		private List<Filter> filters = new ArrayList<>();
		private Sink sink;

		public Source getSource() {
			return source;
		}

		public void setSource(Source source) {
			this.source = source;
		}

		public List<Filter> getFilters() {
			return filters;
		}

		public void addFilter(Filter filter) {
			this.filters.add(filter);
		}

		public Sink getSink() {
			return sink;
		}

		public void setSink(Sink sink) {
			this.sink = sink;
		}
	}
}
