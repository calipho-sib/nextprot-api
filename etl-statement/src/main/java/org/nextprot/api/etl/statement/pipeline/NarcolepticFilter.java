package org.nextprot.api.etl.statement.pipeline;


import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementBuilder;
import org.nextprot.commons.statements.specs.CustomStatementField;

import java.io.IOException;

public class NarcolepticFilter extends PipedFilter {

	private static int COUNT;

	private final int takeRestInMillis;

	public NarcolepticFilter(int crossSection) {

		this(crossSection, 1000);
	}

	public NarcolepticFilter(int crossSection, int takeRestInMillis) {

		super(crossSection);
		COUNT++;

		this.takeRestInMillis = takeRestInMillis;
	}

	@Override
	public String getName() {
		return "ExampleFilter-"+COUNT;
	}

	@Override
	public boolean filter(PipedInputPort in, PipedOutputPort out) throws IOException {

		Statement[] buffer = new Statement[crossSection];

		int numOfStatements = in.read(buffer, 0, crossSection);

		for (int i=0 ; i<numOfStatements ; i++) {

			if (buffer[i] == null) {

				return true;
			}

			System.out.println(Thread.currentThread().getName()
					+ ": filter statement "+ buffer[i].getStatementId());

			out.write(new StatementBuilder(buffer[i])
					.addField(new CustomStatementField("FILTER"), "ExampleFilter")
					.build());
		}

		if (takeRestInMillis > 0) {
			try {
				Thread.sleep(takeRestInMillis);
			} catch (InterruptedException e) {
				System.err.println(e.getMessage());
			}
		}

		return false;
	}
}
