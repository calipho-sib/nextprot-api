package org.nextprot.api.etl.statement.pipeline;


import org.junit.Test;
import org.nextprot.commons.statements.Statement;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

public class PipelineBuilderTest {

	@Test
	public void testPipeline() throws IOException {

		URL url = new URL("http://kant.sib.swiss:9001/glyconnect/2019-01-22/all-entries.json");
		Reader reader = new InputStreamReader(url.openStream());
		Pump<Statement> pump = new StatementPump(reader, 10);

		Pipeline pipeline = new PipelineBuilder()
				.start()
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