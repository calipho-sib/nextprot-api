package org.nextprot.api.etl.statement.pipeline;


import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementBuilder;
import org.nextprot.commons.statements.specs.CustomStatementField;

public class ExampleFilter extends SimpleFilter {

	public ExampleFilter(Pipe input, Pipe output) {

		super(input, output);
	}

	protected Statement transformStatement(Statement in) {
		delayForDebug(100);

		return new StatementBuilder(in)
				.addField(new CustomStatementField("sponge"), "bob")
				.build();
	}
}
