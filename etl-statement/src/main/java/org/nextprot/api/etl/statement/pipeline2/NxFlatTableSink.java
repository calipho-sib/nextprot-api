package org.nextprot.api.etl.statement.pipeline2;

import org.nextprot.commons.statements.Statement;

import java.io.IOException;

public class NxFlatTableSink extends Pipe {

	enum Table {

		raw_statements,
		entry_mapped_statements
	}

	private final Table table;

	public NxFlatTableSink(Table table) {
		super();

		this.table = table;
	}

	/**
	 * This is the thread body for this sink.  When the pipe is started, it
	 * copies characters from the pipe into the specified Writer.
	 **/
	public void run() {
		try {
			Statement statement;

			int i = 0;
			while ((statement = in.read()) != null) {
				System.out.println("write statement " + statement.getStatementId() + " in table " + table);
				i++;
			}
			System.out.println("statements read: " + i);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		// When done with the data, close the pipe and flush the Writer
		finally {
			try {
				in.close();
				out.flush();
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
	}
}