package org.nextprot.api.etl.statement.pipeline;

import org.nextprot.commons.statements.Statement;

import java.io.IOException;

public class NxFlatTableSink extends PipedSink {

	enum Table {

		raw_statements,
		entry_mapped_statements
	}

	private final Table table;

	public NxFlatTableSink(Table table) {
		super(1);

		this.table = table;
	}

	@Override
	public void handleFlow() throws IOException {

		Statement statement;

		int i = 0;
		while ((statement = in.read()) != null) {
			System.out.println(Thread.currentThread().getName() + ": write statement " + statement.getStatementId()
					+ " in table " + table);
			i++;
		}
		System.out.println(Thread.currentThread().getName() + ": " + i + " statements evacuated");
	}
}