package org.nextprot.api.etl.statement.source;


import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.commons.statements.reader.JsonStatementReader;
import org.nextprot.commons.statements.reader.StatementReader;
import org.nextprot.commons.statements.specs.Specifications;
import sun.net.www.protocol.http.HttpURLConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Base implementation of an http Server that is a source of Statements
 */
public abstract class StatementSourceServer<R extends StatementReader> implements StatementSourceNew {

	private static final Log LOGGER = LogFactory.getLog(StatementSourceServer.class);

	private final String sourceName;
	private final String hostName;
	private final String releaseDate;
	private final String homeStatementsURL;
	private final Specifications specifications;

	public StatementSourceServer(String sourceName, String releaseDate, Specifications specifications) throws IOException {

		this(sourceName, "http://kant.sib.swiss:9001", releaseDate, specifications);
	}

	public StatementSourceServer(String sourceName, String hostName, String releaseDate, Specifications specifications) throws IOException {

		if (!isServiceUp(new URL(hostName))) {
			throw new NextProtException("Cannot connect to the statement source " + sourceName + " at host " + hostName
					+ ": service is down");
		}

		this.homeStatementsURL = homeStatementsURL();

		if (!isServiceUp(new URL(homeStatementsURL))) {
			throw new NextProtException("Cannot get statements from the source " + sourceName + " at unknown release date '" + releaseDate + "'");
		}

		this.sourceName = sourceName;
		this.hostName = hostName;
		this.releaseDate = releaseDate;
		this.specifications = specifications;
	}

	@Override
	public Specifications specifications() {

		return specifications;
	}

	/**
	 * @return a stream of Readers that read statements
	 * @throws IOException
	 */
	public Stream<R> readers() throws IOException {

		return parseJsonStatementsUrls().stream()
				.map(url -> {
					try {
						return reader(url);
					} catch (IOException e) {
						LOGGER.error("Cannot open url " + url + ": " + e.getMessage());
						return null;
					}
				})
				.filter(Objects::nonNull);
	}

	private String homeStatementsURL() {

		return hostName + "/" + sourceName.toLowerCase() + "/" + releaseDate;
	}

	private List<URL> parseJsonStatementsUrls() throws IOException {

		List<URL> jsonStatementsUrls = new ArrayList<>();

		InputStream is = new URL(homeStatementsURL).openStream();

		if (is != null) {

			String content = IOUtils.toString(is, "UTF8");
			Pattern jsonListPattern = Pattern.compile("href=\"(.*.json)\"", Pattern.MULTILINE);
			Matcher matcher = jsonListPattern.matcher(content);
			while (matcher.find()) {
				jsonStatementsUrls.add(new URL(homeStatementsURL + "/" + matcher.group(1)));
			}

			is.close();
		}

		return jsonStatementsUrls;
	}

	// Default implementation
	protected R reader(URL url) throws IOException {

		return (R) new JsonStatementReader(new InputStreamReader(url.openStream()), specifications);
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
