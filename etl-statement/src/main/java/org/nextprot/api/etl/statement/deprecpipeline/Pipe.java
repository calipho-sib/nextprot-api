package org.nextprot.api.etl.statement.deprecpipeline;

import org.nextprot.commons.statements.Statement;

public interface Pipe {

	boolean spill(Statement statement);

	Statement spillNextOrNullIfEmptied() throws InterruptedException;

	void closeForWriting();
}
