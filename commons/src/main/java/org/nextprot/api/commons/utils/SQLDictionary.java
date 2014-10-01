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
public class SQLDictionary extends FilePatternDictionary {

	public String getSQLQuery(String queryId) {
		return super.getResource(queryId);
	}

	@Override
	protected final String getLocation() {
		return "classpath*:sql-queries/**/*.sql";
	}

	@Override
	protected final String getExtension() {
		return ".sql";
	}

}
