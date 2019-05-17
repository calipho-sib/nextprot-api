package org.nextprot.api.etl.statement.source;


import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.commons.statements.specs.Specifications;
import sun.net.www.protocol.http.HttpURLConnection;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Base implementation of an http Server that is a source of Statements
 */
public class StatementSourceServer implements SplittableStatementSource {

	private static final Log LOGGER = LogFactory.getLog(StatementSourceServer.class);

	private final String sourceName;
	private final String hostName;
	private final String releaseDate;
	private final String homeStatementsURLString;
	private final Specifications specifications;

	public StatementSourceServer(String sourceName, String releaseDate, Specifications specifications) throws IOException {

		this(sourceName, "http://kant.sib.swiss:9001", releaseDate, specifications);
	}

	public StatementSourceServer(String sourceName, String hostName, String releaseDate, Specifications specifications) throws IOException {

		if (!isServiceUp(new URL(hostName))) {
			throw new NextProtException("Cannot connect to the statement source " + sourceName + " at host " + hostName
					+ ": service is down");
		}
		this.sourceName = sourceName;
		this.hostName = hostName;
		this.releaseDate = releaseDate;
		this.specifications = specifications;

		this.homeStatementsURLString = homeStatementsURL();

		if (!isServiceUp(new URL(homeStatementsURLString))) {
			throw new NextProtException("Cannot get statements from the source " + sourceName + " at unknown release date '" + releaseDate + "'");
		}
	}

	public static StatementSourceServer valueOf(String sourceName, String releaseDate) throws IOException {

		switch (sourceName.toLowerCase()) {
			case "bioeditor":
				return BioEditor(releaseDate);
			case "glyconnect":
				return GlyConnect(releaseDate);
			case "gnomad":
				return GnomAD(releaseDate);
			default:
				throw new NextProtException("unknown source name "+sourceName);
		}
	}

	public static StatementSourceServer BioEditor(String releaseDate) throws IOException {

		return new StatementSourceServer("BioEditor", releaseDate, new Specifications.Builder().build());
	}

	public static StatementSourceServer GlyConnect(String releaseDate) throws IOException {

		return new StatementSourceServer("GlyConnect", releaseDate, new Specifications.Builder().build());
	}

	public static StatementSourceServer GnomAD(String releaseDate) throws IOException {

		return new StatementSourceServer("gnomAD", releaseDate, new Specifications.Builder()
				.withExtraFields(Arrays.asList("CANONICAL", "ALLELE_COUNT", "ALLELE_SAMPLED"))
				.withExtraFieldsContributingToUnicityKey(Collections.singletonList("DBSNP_ID"))
				.build());
	}

	@Override
	public Specifications specifications() {

		return specifications;
	}

	@Override
	public Stream<SimpleStatementSource> split() throws IOException {

		return parseJsonStatementsUrls().stream()
				.map(url -> new SimpleStatementSource(specifications, url));
	}

	private String homeStatementsURL() {

		return hostName + "/" + sourceName.toLowerCase() + "/" + releaseDate;
	}

	private List<URL> parseJsonStatementsUrls() throws IOException {

		List<URL> jsonStatementsUrls = new ArrayList<>();

		InputStream is = new URL(homeStatementsURLString).openStream();

		if (is != null) {

			String content = IOUtils.toString(is, "UTF8");
			Pattern jsonListPattern = Pattern.compile("href=\"(.*.json)\"", Pattern.MULTILINE);
			Matcher matcher = jsonListPattern.matcher(content);
			while (matcher.find()) {
				jsonStatementsUrls.add(new URL(homeStatementsURLString + "/" + matcher.group(1)));
			}

			is.close();
		}

		return jsonStatementsUrls;
	}

	private static boolean isServiceUp(URL url) throws IOException {

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setConnectTimeout(3000);
		connection.connect();

		boolean up = connection.getResponseCode() == HttpURLConnection.HTTP_OK;

		connection.disconnect();

		return up;
	}
}
