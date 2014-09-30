package org.nextprot.api.commons.utils;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Utility class that read SQL queries from classpath. 
 * Queries are hold in memory for improved performance
 * 
 * @author dteixeira
 */
@Service
@Lazy
public class SparqlDictionary extends FilePatternDictionary {

	public String getSparqlWithPrefixes(String queryId) {
			return getSparqlPrefixes() + super.getResource(queryId);
	}
	
	public String getSparqlOnly(String queryId) {
		return super.getResource(queryId);
	}
	
	public String getSparqlPrefixes() {
		return super.getResource("prefix");
	}

	@Override
	final String getLocation() {
		return "classpath*:sparql-queries/**/*.rq";
	}

	@Override
	final String getExtension() {
		return ".rq";
	}

}
