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
		super(new PipedStatementReader(1));

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
				System.out.println(Thread.currentThread().getName()+ ": write statement " + statement.getStatementId()
						+ " in table " + table);
				i++;
			}
			System.out.println(Thread.currentThread().getName()+ ": " + i +" statements evacuated");
		}
		catch (IOException e) {
			System.err.println(e.getMessage() + " in thread " + Thread.currentThread().getName());
		}
		// When done with the data, close the pipe and flush the Writer
		finally {
			try {
				in.close();
			} catch (IOException e) {
				System.err.println(e.getMessage() + " in thread " + Thread.currentThread().getName());
			}
		}
	}

	@Override
	public String getName() {
		return "Sink";
	}
}