package org.nextprot.api.etl.statement.pipeline;

import org.nextprot.commons.statements.Statement;

import java.util.function.BiFunction;
import java.util.function.Function;

public class PipelineBuilder implements Pipeline.Builder.Source {

	private final Pipeline.DataCollector dataCollector = new Pipeline.DataCollector();

	@Override
	public Pipeline.Builder.Filter source(Pump<Statement> pump) {

		final Pipe pipeOut = new PipeImpl();

		dataCollector.setPipedSource(new PipedSource(pump, pipeOut));

		return new Filter(dataCollector.getPipedSource());
	}

	public class Filter implements Pipeline.Builder.Filter {

		private final Pipe pipeFrom;

		Filter(PipedSource source) {

			pipeFrom = source.getPipe();
		}

		Filter(PipedFilter previousFilter) {

			pipeFrom = previousFilter.getPipeOut();
		}

		@Override
		public Pipeline.Builder.Filter filter(BiFunction<Pipe, Pipe, PipedFilter> filterProvider) {

			Pipe pipeTo = new PipeImpl();

			PipedFilter pipedFilter = filterProvider.apply(pipeFrom, pipeTo);

			dataCollector.addFilter(pipedFilter);

			return new Filter(pipedFilter);
		}

		@Override
		public Pipeline.Builder.Terminate sink(Function<Pipe, PipedSink> sinkProvider) {

			dataCollector.setSink(sinkProvider.apply(pipeFrom));

			return new Filter.Terminate();
		}

		public class Terminate implements Pipeline.Builder.Terminate {

			@Override
			public Pipeline build() {

				return new Pipeline(dataCollector);
			}
		}
	}

}
