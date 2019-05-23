package org.nextprot.api.etl.statement.pipeline;


import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementBuilder;
import org.nextprot.commons.statements.specs.CoreStatementField;

import java.util.Arrays;

// inspired from code in https://gist.github.com/roryokane/9606238
public class PipelineBuilderTest {

	public static void main(String[] args) {

		// statements
		Statement statement1 = new StatementBuilder().addField(CoreStatementField.ENTRY_ACCESSION, "NX1").build();
		Statement statement2 = new StatementBuilder().addField(CoreStatementField.ENTRY_ACCESSION, "NX2").build();

		// create pipes
		final Pipe pipeIn = new PipeImpl();
		final Pipe pipeOut = new PipeImpl();

		// create components that use the pipes
		final PipedSource pipedSource = new PipedSource(new MockStatementPump(Arrays.asList(statement1, statement2)), pipeIn);
		final PipedFilter filter = new ExampleFilter(pipeIn, pipeOut);
		final PipedSink sink = new NxFlatTableSink(pipeOut, NxFlatTableSink.Table.entry_mapped_statements);

		// start all components
		pipedSource.startPipe();
		filter.startPipe();
		sink.startPipe();

		System.out.println("runner finished");
	}

	@Test
	public void build() {

		Pipeline pipeline = new PipelineBuilder()
				.source(Mockito.mock(StatementPump.class))
				.filter((i, o) -> Mockito.mock(PipedFilter.class))
				.sink((i) -> Mockito.mock(PipedSink.class))
				.build();

		pipeline.start();
	}

	/*@Test
	public void build() throws MalformedURLException {

		PipelineBuilder.Pipeline pipeline = new PipelineBuilder.Source()
				.url(new URL("http://kant.sib.swiss:9001/gnomad/2019-03-14/"))
				.filter((i, o) -> new ExampleFilter(i, o))
				.filter((i, o) -> new ExampleFilter(i, o))
				.sink((i) -> new NxFlatTableSink(i, NxFlatTableSink.Table.entry_mapped_statements))
				.build();
	}*/
}
