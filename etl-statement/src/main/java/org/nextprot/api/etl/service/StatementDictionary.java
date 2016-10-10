package org.nextprot.api.etl.service;

import org.nextprot.api.commons.utils.FilePatternDictionary;
import org.springframework.stereotype.Service;

@Service
public class StatementDictionary extends FilePatternDictionary {

	public String getStatements(String queryId) {
		return super.getResource(queryId);
	}

	@Override
	protected final String getLocation() {
		return "classpath*:statements/**/*.json";
	}

	@Override
	protected final String getExtension() {
		return ".json";
	}

}
