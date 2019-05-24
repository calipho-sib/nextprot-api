package org.nextprot.api.etl.statement.pipeline2;


import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementBuilder;
import org.nextprot.commons.statements.specs.CustomStatementField;

import java.io.IOException;

public class ExampleFilter extends PipeFilter {

	public ExampleFilter(Pipe pipe) throws IOException {

		super(pipe);
	}

	@Override
	public String getName() {
		return "ExampleFilter";
	}

	@Override
	public void filter(PipedStatementReader in, PipedStatementWriter out) throws IOException {

		Statement statement;
		while((statement = in.read()) != null) {
			System.out.println(Thread.currentThread().getName()
					+ ": filter statement "+ statement.getStatementId());

			out.write(new StatementBuilder(statement)
					.addField(new CustomStatementField("FILTER"), "ExampleFilter")
					.build());
		}
	}
}
