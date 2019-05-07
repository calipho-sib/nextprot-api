package org.nextprot.api.etl.service.impl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.etl.StatementSource;
import org.nextprot.api.etl.service.StatementSourceService;
import org.springframework.stereotype.Service;
import sun.net.www.protocol.http.HttpURLConnection;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class StatementSourceServiceImpl implements StatementSourceService {

	private static final Log LOGGER = LogFactory.getLog(StatementSourceServiceImpl.class);

	@Override
	public Set<String> getJsonFilenamesForRelease(StatementSource source, String release) throws IOException {

		Set<String> jsonFilenames = new TreeSet<>();
		String urlString = source.getStatementsUrl() + "/" + release;
		LOGGER.info("Requesting " + urlString);

		if (isServiceUp(urlString)) {
			try (InputStream is = getInputStreamFromUrl(urlString)) {

				if (is != null) {

					String content = IOUtils.toString(is, "UTF8");
					Pattern pattern = Pattern.compile("href\\=\\\"(.*.json)\\\"", Pattern.MULTILINE);
					Matcher matcher = pattern.matcher(content);
					while (matcher.find()) {
						jsonFilenames.add(matcher.group(1));
					}
				}
			} catch (IOException e) {
				throw new NextProtException("Cannot find json filenames for source " + source.getSourceName() + ":" + e.getLocalizedMessage());
			}
		}

		return jsonFilenames;
	}

	@Override
	public String getStatementsAsJsonString(StatementSource source, String release, String jsonFileName) throws IOException {

		String urlString = source.getStatementsUrl() + "/" + release + "/" + jsonFileName;
		if (!jsonFileName.endsWith(".json")) {
			urlString += ".json";
		}

		if (isServiceUp(urlString)) {

			InputStream is = getInputStreamFromUrl(urlString);

			if (is != null) {

				return IOUtils.toString(is, "UTF8");
			}
			else {
				return "[]";
			}
		}

		return "[]";
	}

	private InputStream getInputStreamFromUrl(String urlString) {

		URL url;
		try {
			url = new URL(urlString);
			return url.openStream();
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
		return null;

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
