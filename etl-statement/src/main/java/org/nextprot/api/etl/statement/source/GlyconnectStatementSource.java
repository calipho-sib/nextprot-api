package org.nextprot.api.etl.statement.source;

import org.nextprot.commons.statements.specs.Specifications;

import java.io.IOException;

public class GlyconnectStatementSource extends StatementSourceServer {

	public GlyconnectStatementSource(String releaseDate) throws IOException {

		super("GlyConnect", releaseDate, new Specifications.Builder().build());
	}
}
