package org.nextprot.api.etl.statement.pipeline;


import org.junit.Test;
import org.nextprot.commons.statements.Statement;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

// inspired from code in https://gist.github.com/roryokane/9606238
public class PipelineBuilderTest {

	@Test
	public void testWihoutPipeline() throws IOException {

		URL url = new URL("http://kant.sib.swiss:9001/glyconnect/2019-01-22/all-entries.json");
		Reader reader = new InputStreamReader(url.openStream());

		Pump<Statement> pump = new StatementPump(reader, 10);

		PipedSource source = new PipedSource(pump);

		NarcolepticFilter filter = new NarcolepticFilter(10, -1);
		source.connect(filter);

		NxFlatTableSink sink = new NxFlatTableSink(NxFlatTableSink.Table.entry_mapped_statements);
		filter.connect(sink);

		// Start the pipe -- start each of the threads in the pipe running.
		// This call returns quickly, since the each component of the pipe
		// is its own thread
		System.out.println("Starting the source pipe...");
		source.openPipe();

		// Wait for the pipe to complete
		try {
			source.waitForThePipesToComplete();
		} catch (InterruptedException e) {
		}

		System.out.println("Done.");
	}

	@Test
	public void testPipeline() throws IOException {

		URL url = new URL("http://kant.sib.swiss:9001/glyconnect/2019-01-22/all-entries.json");
		Reader reader = new InputStreamReader(url.openStream());
		Pump<Statement> pump = new StatementPump(reader, 10);

		Pipeline pipeline = new PipelineBuilder()
				.source(pump)
				.filter((c) -> new NarcolepticFilter(c))
				.filter((c) -> new NarcolepticFilter(c))
				.sink((c) -> new NxFlatTableSink(NxFlatTableSink.Table.entry_mapped_statements))
				.build();

		pipeline.open();

		// Wait for the pipe to complete
		try {
			pipeline.waitForThePipesToComplete();
		} catch (InterruptedException e) {
			System.err.println("pipeline error: "+e.getMessage());
		}
		System.out.println("Done.");
	}
}