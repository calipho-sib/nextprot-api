package org.nextprot.api.etl.statement.pipeline;

import org.nextprot.commons.statements.Statement;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

public class PipeTest {
	/**
	 * This class contains a test program for the pipe classes below.
	 * It also demonstrates how you typically use these pipes classes.
	 * It is basically another implementation of a Unix-like grep command.
	 * Note that it frivolously passes the output of the grep filter through
	 * two rot13 filters (which, combined, leave the output unchanged).
	 * Then it converts non-ASCII characters to their \U Unicode encodings.
	 *
	 * With the pipe infrastructure defined below, it is easy to define
	 * new filters and create pipes to perform many useful operations.
	 * Other filter possibilities include sorting lines, removing
	 * duplicate lines, and doing search-and-replace.
	 **/
	public static class Test {

		/** This is the test program for our pipes infrastructure */
		public static void main(String[] args) throws IOException {

			URL url = new URL("http://kant.sib.swiss:9001/glyconnect/2019-01-22/all-entries.json");
			Reader reader = new InputStreamReader(url.openStream());

			Pump<Statement> pump = new StatementPump(reader, 10);

			Source source = new Source(pump);

			ExampleFilter filter = new ExampleFilter(10);
			source.connect(filter);

			NxFlatTableSink sink = new NxFlatTableSink(NxFlatTableSink.Table.entry_mapped_statements);
			filter.connect(sink);

			// Start the pipe -- start each of the threads in the pipe running.
			// This call returns quickly, since the each component of the pipe
			// is its own thread
			System.out.println("Starting the source pipe...");
			source.openPipe();

			// Wait for the pipe to complete
			try { source.joinPipe(); } catch (InterruptedException e) {}
			System.out.println("Done.");
		}
	}
}
