package org.nextprot.api.etl.statement.deprecpipeline;

import org.nextprot.commons.statements.Statement;

public abstract class SimpleFilter extends PipedFilter {

	public SimpleFilter(Pipe input, Pipe output) {
		super(input, output);
	}

	@Override
	protected void transform(Pipe input, Pipe output) {
		try {
			Statement statement;
			while ((statement = input.spillNextOrNullIfEmptied()) != null) {
				Statement out = transformStatement(statement);
				output.spill(out);
			}
		} catch (InterruptedException e) {
			// TODO handle properly, using advice in http://www.ibm.com/developerworks/java/library/j-jtp05236/
			System.err.println("interrupted");
			e.printStackTrace();
			return;
		}
		output.closeForWriting();
	}

	protected abstract Statement transformStatement(Statement in);
}