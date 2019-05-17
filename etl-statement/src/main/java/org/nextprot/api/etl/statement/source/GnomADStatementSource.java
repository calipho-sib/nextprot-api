package org.nextprot.api.etl.statement.source;

import org.nextprot.commons.statements.reader.StreamingJsonStatementReader;
import org.nextprot.commons.statements.specs.Specifications;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;

public class GnomADStatementSource extends StatementSourceServer<StreamingJsonStatementReader> {

	public GnomADStatementSource(String releaseDate) throws IOException {

		super("gnomAD", releaseDate, new Specifications.Builder()
				.withExtraFields(Arrays.asList("CANONICAL", "ALLELE_COUNT", "ALLELE_SAMPLED"))
				.withExtraFieldsContributingToUnicityKey(Collections.singletonList("DBSNP_ID"))
				.build());
	}

	@Override
	protected StreamingJsonStatementReader reader(URL url) throws IOException {

		return new StreamingJsonStatementReader(new InputStreamReader(url.openStream()),
				specifications(), 100);
	}
}
