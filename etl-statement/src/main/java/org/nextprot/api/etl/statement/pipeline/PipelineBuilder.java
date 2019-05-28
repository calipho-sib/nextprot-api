package org.nextprot.api.etl.statement.pipeline;

import org.nextprot.commons.statements.Statement;

public class PipelineBuilder implements Pipeline.Builder.Source {

	private final Pipeline.DataCollector dataCollector = new Pipeline.DataCollector();

	@Override
	public Pipeline.Builder.Filter source(Pump<Statement> pump) {

		final Source source = new Source(pump);

		dataCollector.setSource(source);

		return null; //new Filter(dataCollector.getPipedSource());
	}

	/*public class Filter implements Pipeline.Builder.Filter {

		private final Pipe source;

		Filter(Pipe source) {

			this.source = source;
		}

		@Override
		public Pipeline.Builder.Filter filter(BiFunction<Pipe, Pipe, PipeFilter> filterProvider) {

			PipedFilter pipedFilter = filterProvider.apply(source, pipeTo);

			dataCollector.addFilter(pipedFilter);

			return new Filter(pipedFilter);
		}

		@Override
		public Pipeline.Builder.Terminate sink(Function<Pipe, Sink> sinkProvider) {

			dataCollector.setSink(sinkProvider.apply(pipeFrom));

			return new Filter.Terminate();
		}

		public class Terminate implements Pipeline.Builder.Terminate {

			@Override
			public Pipeline build() {

				return new Pipeline(dataCollector);
			}
		}
	}*/

}
