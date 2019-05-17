package org.nextprot.api.etl.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.etl.StatementSourceEnum;
import org.nextprot.api.etl.service.StatementExtractorService;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.reader.JsonStatementReader;
import org.springframework.stereotype.Service;
import sun.net.www.protocol.http.HttpURLConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class StatementRemoteServiceImpl implements StatementExtractorService {

	private static final Log LOGGER = LogFactory.getLog(StatementRemoteServiceImpl.class);

	// BioEditor Raw Statement service for a Gene. Example for msh2:
	// http://kant.isb-sib.ch:9000/bioeditor/gene/msh2/statements
	public Collection<Statement> getStatementsFromJsonFile(StatementSourceEnum source, String release, String jsonFileName) throws IOException {

		String urlString = source.getStatementsUrl() + "/" + release + "/" + jsonFileName;
		if (!jsonFileName.endsWith(".json")) {
			urlString += ".json";
		}

		if (isServiceUp(urlString)) {

			return new JsonStatementReader(readUrlContent(urlString), source.getSpecifications()).readStatements();
		}

		return new HashSet<>();
	}

	// BioEditor Raw Statement service for all data (CAREFUL WITH THIS ONE)
	// http://kant.isb-sib.ch:9000/bioeditor/statements
	public Collection<Statement> getStatementsForSource(StatementSourceEnum source, String release) throws IOException {

		Set<Statement> statements = new LinkedHashSet<>();
		getJsonFilenamesForRelease(source, release)
				.forEach(jsonFilename -> {
					try {
						statements.addAll(getStatementsFromJsonFile(source, release, jsonFilename));
					} catch (IOException e) {
						throw new NextProtException(e.getMessage());
					}
				});
		return statements;
	}

	Set<String> getJsonFilenamesForRelease(StatementSourceEnum source, String release) throws IOException {
		Set<String> genes = new TreeSet<>();
		String urlString = source.getStatementsUrl() + "/" + release;
		LOGGER.info("Requesting " + urlString);

		if (isServiceUp(urlString)) {
			String content = readUrlContent(urlString);

			if (content != null) {

				Pattern pattern = Pattern.compile("href\\=\\\"(.*.json)\\\"", Pattern.MULTILINE);
				Matcher matcher = pattern.matcher(content);
				while (matcher.find()) {
					genes.add(matcher.group(1));
				}
			}
		}

		return genes;
	}

	private String readUrlContent(String urlString) throws IOException {

		URL url = new URL(urlString);

		try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8.name()))) {

			return br.lines().collect(Collectors.joining(System.lineSeparator()));
		}
	}

	private boolean isServiceUp(String url) throws IOException {

		URL nxflatServerURL = new URL(url);

		HttpURLConnection connection = (HttpURLConnection) nxflatServerURL.openConnection();
		connection.setRequestMethod("GET");
		connection.setConnectTimeout(3000);
		connection.connect();

		boolean up = connection.getResponseCode() == HttpURLConnection.HTTP_OK;

		connection.disconnect();

		return up;
	}
}
