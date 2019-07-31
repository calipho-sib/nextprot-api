package org.nextprot.api.etl.pipeline;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.etl.pipeline.pump.HttpStatementPump;

import java.io.IOException;
import java.util.stream.Stream;

@Ignore
public class StatementSourceProxyIntegrationTest {

	@Test
	public void glyconnectProxyShouldProvideOnePump() throws IOException {

		StatementSourceProxy proxy = StatementSourceProxy.GlyConnect("2019-01-22", 100, 2);
		Stream<HttpStatementPump> pumps = proxy.createPumps();
		Assert.assertEquals(1, pumps.count());
	}

	@Test
	public void execute() throws IOException {

		StatementSourceProxy proxy = StatementSourceProxy.GlyConnect("2019-01-22", 100, 2);

		proxy.executePipelines(2);
	}

	@Test
	public void bioeditorProxyShouldProvideNPumps() throws IOException {

		StatementSourceProxy proxy = StatementSourceProxy.BioEditor("2019-01-22", 100, 2);
		Stream<HttpStatementPump> pumps = proxy.createPumps();
		Assert.assertEquals(487, pumps.count());
	}

/*
	@Test
	public void testBioEditorExecution() throws IOException, InterruptedException {

		StatementSourceProxy proxy = StatementSourceProxy.BioEditor("2019-01-22", 100, 2);
		proxy.executePipelines(2);
	}


	// 4s
	@Test
	public void testReadingOneSimpleStatementSource() throws IOException {

		StatementSourceProxy source = StatementSourceProxy.BioEditor("2019-01-22");

		List<SimpleStatementSource> sources = source.streamPumps()
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

		StatementSourceProxy source = StatementSourceProxy.BioEditor("2019-01-22");

		source.streamPumps().forEach(s -> {
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

		StatementSourceProxy source = StatementSourceProxy.BioEditor("2019-01-22");

		source.streamPumps().parallel().forEach(s -> {
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

		StatementSourceProxy source = StatementSourceProxy.BioEditor("2019-01-22");

		Stream<SimpleStatementSource> sources = source.streamPumps();

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
	}*/
}