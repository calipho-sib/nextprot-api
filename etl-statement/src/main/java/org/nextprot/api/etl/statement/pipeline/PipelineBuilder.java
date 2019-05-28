package org.nextprot.api.etl.statement.pipeline;

import org.nextprot.commons.statements.Statement;

import java.io.IOException;
import java.util.function.Function;

public class PipelineBuilder implements Pipeline.Builder.Source {

	private final Pipeline.DataCollector dataCollector = new Pipeline.DataCollector();

	@Override
	public Pipeline.Builder.Filter source(Pump<Statement> pump) {

		final PipedSource source = new PipedSource(pump);
		dataCollector.setSource(source);

		return new Filter(source);
	}

	public class Filter implements Pipeline.Builder.Filter {

		private final Pipe source;

		Filter(Pipe source) {

			this.source = source;
		}

		@Override
		public Pipeline.Builder.Filter filter(Function<Integer, PipedFilter> filterProvider) throws IOException {

			PipedFilter pipedFilter = filterProvider.apply(dataCollector.getSource().pump.capacity());
			source.connect(pipedFilter);

			return new Filter(pipedFilter);
		}

		@Override
		public Pipeline.Builder.Terminate sink(Function<Integer, PipedSink> sinkProvider) throws IOException {

			PipedSink sink = sinkProvider.apply(1);
			source.connect(sink);

			return new Filter.Terminate();
		}

		public class Terminate implements Pipeline.Builder.Terminate {

			@Override
			public Pipeline build() throws IOException {

				return new Pipeline(dataCollector);
			}
		}
	}

}
