package org.nextprot.api.etl.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.constants.NextProtSource;
import org.springframework.stereotype.Service;

@Service
public class StatementRemoteServiceImpl extends StatementExtractorBase {

	private static final Log LOGGER = LogFactory.getLog(StatementRemoteServiceImpl.class);
	
	private String serviceUrl = "http://kant.isb-sib.ch:9000";

	// BioEditor Raw Statement service for a Gene. Example for msh2:
	// http://kant.isb-sib.ch:9000/bioeditor/gene/msh2/statements
	public Set<Statement> getStatementsForSourceForGeneName(NextProtSource source, String geneName) {

		String urlString = source.getStatementsUrl() + "/gene/" + geneName + "/statements";
		return deserialize(getInputStreamFromUrl(urlString));
	}

	// BioEditor Raw Statement service for all data (CAREFUL WITH THIS ONE)
	// http://kant.isb-sib.ch:9000/bioeditor/statements
	public Set<Statement> getStatementsForSource(NextProtSource source) {

		String urlString = source.getStatementsUrl() + "/statements";
		return deserialize(getInputStreamFromUrl(urlString));

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
