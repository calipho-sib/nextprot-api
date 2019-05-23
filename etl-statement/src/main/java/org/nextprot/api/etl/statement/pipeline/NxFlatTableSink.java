package org.nextprot.api.etl.statement.pipeline;

import org.nextprot.commons.statements.Statement;

public class NxFlatTableSink extends PipedSink {

	enum Table {

		raw_statements,
		entry_mapped_statements
	}

	private final Table table;

	public NxFlatTableSink(Pipe input, Table table) {
		super(input);

		this.table = table;
	}

	@Override
	public void takeFrom(Pipe pipe) {
		try {
			Statement in;
			while ((in = pipe.spillNextOrNullIfEmptied()) != null) {
				System.out.println("write statement " + in + " in table "+ table);
				delayForDebug(300);
			}
			System.out.println("sink finished");
		} catch (InterruptedException e) {
			System.err.println("interrupted");
			e.printStackTrace();
		} finally {
			System.out.close();
		}
	}
}