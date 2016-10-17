package org.nextprot.api.etl.service;

import org.nextprot.api.commons.utils.FilePatternDictionary;
import org.springframework.stereotype.Service;

@Service
public class IsoMapperDictionary extends FilePatternDictionary {

	public String getIsoMapperResponse(String featureName) {
		return super.getResource(featureName);
	}

	@Override
	protected final String getLocation() {
		return "classpath*:isomapper/**/*.json";
	}

	@Override
	protected final String getExtension() {
		return ".json";
	}

}
