package org.nextprot.api.etl.statement.source;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.commons.statements.Statement;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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

	@Test
	public void testReading() throws IOException {

		StatementSourceServer source = StatementSourceServer.BioEditor("2019-01-22");

		Stream<SimpleStatementSource> sources = source.split();

		SimpleStatementSource aSource = sources.collect(Collectors.toList()).get(0);

		Assert.assertTrue(aSource.hasStatement());
	}

	// 4s
	@Test
	public void testReadingOneSimpleStatementSource() throws IOException {

		StatementSourceServer source = StatementSourceServer.BioEditor("2019-01-22");

		List<SimpleStatementSource> sources = source.split()
				.collect(Collectors.toList());

		for (SimpleStatementSource s : sources) {
			while (s.hasStatement()) {

				s.nextStatement();
			}

			Assert.assertFalse(s.hasStatement());
		}
	}

	// 4s
	@Test
	public void testReadingOneSimpleStatementSource2() throws IOException {

		StatementSourceServer source = StatementSourceServer.BioEditor("2019-01-22");

		source.split().forEach(s -> {
			try {
				while (s.hasStatement()) {

					s.nextStatement();
				}

				Assert.assertFalse(s.hasStatement());
			}catch (IOException e) {
				System.err.println(e.getMessage());
			}
		});
	}

	// 4s
	@Test
	public void testReadingOneSimpleStatementSource3() throws IOException {

		StatementSourceServer source = StatementSourceServer.BioEditor("2019-01-22");

		source.split().parallel().forEach(s -> {
			try {
				while (s.hasStatement()) {

					s.nextStatement();
				}

				Assert.assertFalse(s.hasStatement());
			}catch (IOException e) {
				System.err.println(e.getMessage());
			}
		});
	}

	@Test
	public void testSplitReading() throws IOException {

		StatementSourceServer source = StatementSourceServer.BioEditor("2019-01-22");

		Stream<SimpleStatementSource> sources = source.split();

		List<Statement> firstStatements = sources
				.map(s -> {
					try {
						return s.nextStatement();
					} catch (IOException e) {
						e.printStackTrace();
						return null;
					}
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toList());

		System.out.println(firstStatements);
	}
}