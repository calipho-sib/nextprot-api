package org.nextprot.api.commons.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * Utility class that read SQL queries from a directory. $
 * Queries are hold in memory for improved performance
 * 
 * @author dteixeira
 */
public class SQLDictionary {

	private static SQLDictionary singleton = null;
	static {
		singleton = new SQLDictionary();
		singleton.initialize();
	}

	private Map<String, String> sqlQueries = null;

	private void initialize() {

		sqlQueries = new HashMap<String, String>();

		Resource[] resources;
		try {
			resources = new PathMatchingResourcePatternResolver().getResources("classpath:sql/**/*.sql");
			mapQueries(resources, sqlQueries);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void mapQueries(Resource[] resources, Map<String, String> queryMap) throws IOException {

		for (Resource r : resources) {
			queryMap.put(r.getFilename().replace(".sql", ""), FileUtils.readFileAsString(r.getFile().getAbsolutePath()));
		}
	}


	/**
	 * Gets the query
	 * 
	 * @param queryId
	 * @return
	 */
	public static String getSQLQuery(String queryId) {
		return singleton.sqlQueries.get(queryId);
	}

}
