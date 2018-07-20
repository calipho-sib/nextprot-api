package org.nextprot.api.etl.service.impl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.constants.NextProtSource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class StatementRemoteServiceImpl extends StatementExtractorBase {

	private static final Log LOGGER = LogFactory.getLog(StatementRemoteServiceImpl.class);

	// BioEditor Raw Statement service for a Gene. Example for msh2:
	// http://kant.isb-sib.ch:9000/bioeditor/gene/msh2/statements
	public Set<Statement> getStatementsFromJsonFile(NextProtSource source, String release, String jsonFileName) {

		String urlString = source.getStatementsUrl() + "/" + release + "/" + jsonFileName;

		if (!jsonFileName.endsWith(".json")) {

            urlString += ".json";
        }

		return deserialize(getInputStreamFromUrl(urlString));
	}

	// BioEditor Raw Statement service for all data (CAREFUL WITH THIS ONE)
	// http://kant.isb-sib.ch:9000/bioeditor/statements
	public Set<Statement> getStatementsForSource(NextProtSource source, String release) {

		Set<Statement> statements = new LinkedHashSet<>();
        getJsonFilenamesForRelease(source, release)
                .forEach(jsonFilename -> statements.addAll(getStatementsFromJsonFile(source, release, jsonFilename)));
		return statements;
	}

	Set<String> getJsonFilenamesForRelease(NextProtSource source, String release) {
		Set<String> genes = new TreeSet<>();
		String urlString = source.getStatementsUrl() + "/" + release;
		LOGGER.info("Requesting " +  urlString );

        try (InputStream is = getInputStreamFromUrl(urlString)) {

            if (is != null) {

                String content = IOUtils.toString(is, "UTF8");
                Pattern pattern = Pattern.compile("href\\=\\\"(.*.json)\\\"", Pattern.MULTILINE);
                Matcher matcher = pattern.matcher(content);
                while (matcher.find()) {
                    genes.add(matcher.group(1));
                }
            }
        }
        catch (IOException e) {
            throw new NextProtException("Cannot find json filenames for source "+ source.getSourceName() + ":" + e.getLocalizedMessage());
        }

		return genes;
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

}
