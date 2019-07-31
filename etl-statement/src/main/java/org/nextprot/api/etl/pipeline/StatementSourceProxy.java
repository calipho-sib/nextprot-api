package org.nextprot.api.etl.pipeline;


import org.apache.commons.io.IOUtils;
import org.nextprot.api.etl.pipeline.filter.NonPhenotypicVariationStatementsFilter;
import org.nextprot.api.etl.pipeline.filter.NxFlatRawTableFilter;
import org.nextprot.api.etl.pipeline.pump.HttpStatementPump;
import org.nextprot.api.etl.pipeline.sink.NxFlatMappedTableSink;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.specs.Specifications;
import org.nextprot.commons.statements.specs.StatementSpecifications;
import org.nextprot.pipeline.statement.core.Pipeline;
import org.nextprot.pipeline.statement.core.PipelineBuilder;
import org.nextprot.pipeline.statement.core.stage.source.Pump;
import sun.net.www.protocol.http.HttpURLConnection;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A statement source proxy make HTTP requests to kant and produces multiple sources of Statements
 */
public class StatementSourceProxy {

	private static final Pattern JSON_LIST_PATTERN = Pattern.compile("href=\"(.*.json)\"", Pattern.MULTILINE);

	private final String sourceName;
	private final String hostName = "http://kant.sib.swiss:9001";
	private final String releaseDate;
	private final String homeStatementsURLString;
	private final StatementSpecifications specifications;
	private final Function<Pump<Statement>, Pipeline> pipelineBuilder;

	private StatementSourceProxy(String sourceName, String releaseDate, StatementSpecifications specifications,
	                             Function<Pump<Statement>, Pipeline> pipelineBuilder) throws IOException {

		if (isServerDown(hostName)) {
			throw new IllegalArgumentException("Cannot connect to the statement source " + sourceName + " at host " + hostName
					+ ": service is down");
		}
		this.sourceName = sourceName;
		this.releaseDate = releaseDate;

		this.homeStatementsURLString = homeStatementsURL();
		this.specifications = specifications;
		this.pipelineBuilder = pipelineBuilder;

		if (isServerDown(homeStatementsURLString)) {
			throw new IllegalArgumentException("Cannot get statements from the source " + sourceName + " at unknown release date '" + releaseDate + "'");
		}
	}

	public static StatementSourceProxy BioEditor(String releaseDate, int sourceCapacity, int duplication) throws IOException {

		return new StatementSourceProxy("BioEditor", releaseDate, new Specifications.Builder().build(),
				pump -> new PipelineBuilder()
						.start()
						.source(pump, sourceCapacity)
						.split(NonPhenotypicVariationStatementsFilter::new, duplication)
						.filter(NxFlatRawTableFilter::new)
						.sink(NxFlatMappedTableSink::new)
						.build());
	}

	public static StatementSourceProxy GlyConnect(String releaseDate, int sourceCapacity, int duplication) throws IOException {

		return new StatementSourceProxy("GlyConnect", releaseDate, new Specifications.Builder().build(),
				pump -> new PipelineBuilder()
						.start()
						.source(pump, sourceCapacity)
						.split(NxFlatRawTableFilter::new, duplication)
						.sink(NxFlatMappedTableSink::new)
						.build());
	}

	public static StatementSourceProxy GnomAD(String releaseDate, int sourceCapacity, int duplication) throws IOException {

		return new StatementSourceProxy("gnomAD", releaseDate, new Specifications.Builder()
				.withExtraFields(Arrays.asList("CANONICAL", "ALLELE_COUNT", "ALLELE_SAMPLED"))
				.withExtraFieldsContributingToUnicityKey(Collections.singletonList("DBSNP_ID"))
				.build(),
				pump -> new PipelineBuilder()
						.start()
						.source(pump, sourceCapacity)
						.split(NxFlatRawTableFilter::new, duplication)
						.sink(NxFlatMappedTableSink::new)
						.build());
	}

	public Map<String, Future<Long>> executePipelines(int nThreads) {

		ExecutorService executor = Executors.newFixedThreadPool(nThreads);

		Map<String, Future<Long>> futures = new HashMap<>();

		List<HttpStatementPump> pumps = createPumps().collect(Collectors.toList());

		CountDownLatch latch = new CountDownLatch(pumps.size());

		pumps.forEach(pump ->
				futures.put(pump.getUrl(), executor.submit(new TimedPipelineTask(pump, pipelineBuilder, latch)))
		);

		try {
			latch.await();
		} catch (InterruptedException E) {
			// handle
		}

		return futures;
	}

	Stream<HttpStatementPump> createPumps() {

		return extractAllJsonUrls().stream()
				.map(url -> new HttpStatementPump(url, specifications));
	}

	private String homeStatementsURL() {

		return hostName + "/" + sourceName.toLowerCase() + "/" + releaseDate;
	}

	private List<String> extractAllJsonUrls() {

		List<String> allJsonUrls = new ArrayList<>();

		try {
			URLConnection connection = new URL(homeStatementsURLString).openConnection();
			String content = IOUtils.toString(connection.getInputStream(), "UTF8");

			Matcher matcher = JSON_LIST_PATTERN.matcher(content);
			while (matcher.find()) {
				allJsonUrls.add(homeStatementsURLString + "/" + matcher.group(1));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return allJsonUrls;
	}

	private static boolean isServerDown(String url) throws IOException {

		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setRequestMethod("HEAD");
		connection.setConnectTimeout(3000);
		connection.setReadTimeout(3000);

		try {
			connection.connect();

			return connection.getResponseCode() != HttpURLConnection.HTTP_OK;
		} catch (IOException e) {

			throw new IOException("statement service " + url + " does not respond: " + e.getMessage());
		} finally {

			connection.disconnect();
		}
	}

	private static class TimedPipelineTask implements Callable<Long> {

		private final HttpStatementPump pump;
		private final Function<Pump<Statement>, Pipeline> pipelineBuilder;
		private final CountDownLatch latch;

		public TimedPipelineTask(HttpStatementPump pump, Function<Pump<Statement>, Pipeline> pipelineBuilder,
		                         CountDownLatch latch) {

			this.pump = pump;
			this.pipelineBuilder = pipelineBuilder;
			this.latch = latch;
		}

		@Override
		public Long call() {

			Timer timer = new Timer();

			Pipeline pipeline = pipelineBuilder.apply(pump);

			pipeline.openValves();

			// wait for the pipeline to complete
			try {
				pipeline.waitUntilCompletion();
			} catch (InterruptedException e) {
				System.err.println(Thread.currentThread().getName() + " pipeline error: " + e.getMessage());
			}

			latch.countDown();

			return timer.getElapsedTimeInMs();
		}
	}


}
