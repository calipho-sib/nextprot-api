package org.nextprot.api.etl.statement.source;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.stream.Stream;

public class StatementSourceServerTest {

	@Test
	public void testConstr() throws IOException {

		StatementSourceServer source = StatementSourceServer.BioEditor("2019-01-22");

		Assert.assertNotNull(source.specifications());
	}

	@Test
	public void testSplitCount() throws IOException {

		StatementSourceServer source = StatementSourceServer.BioEditor("2019-01-22");

		Stream<SimpleStatementSource> sources = source.split();

		Assert.assertEquals(487, sources.count());
	}
}