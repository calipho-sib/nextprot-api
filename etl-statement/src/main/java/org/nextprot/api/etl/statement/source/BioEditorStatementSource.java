package org.nextprot.api.etl.statement.source;

import org.nextprot.commons.statements.specs.Specifications;

import java.io.IOException;

public class BioEditorStatementSource extends StatementSourceServer {

	public BioEditorStatementSource(String releaseDate) throws IOException {

		super("BioEditor", releaseDate, new Specifications.Builder().build());
	}
}
