package org.nextprot.api.commons.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.nextprot.api.commons.exception.NextProtException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

/**
 * Utility class that read SQL queries from a directory. $
 * Queries are hold in memory for improved performance
 * 
 * @author dteixeira
 */
public class SQLDictionary {

	private static Logger log = Logger.getLogger(SQLDictionary.class);

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
			//ClassLoader cl = this.getClass().getClassLoader();
			resources = new PathMatchingResourcePatternResolver().getResources("classpath*:sql-queries/**/*.sql");
			mapQueries(resources, sqlQueries);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void mapQueries(Resource[] resources, Map<String, String> queryMap) throws IOException {
		for (Resource r : resources) {
			queryMap.put(r.getFilename().replace(".sql", ""), Resources.toString(r.getURL(), Charsets.UTF_8));
		}
	}

	/**
	 * Gets the query
	 * 
	 * @param queryId
	 * @return
	 */
	public static String getSQLQuery(String queryId) {
		if (singleton.sqlQueries.containsKey(queryId)) {
			return singleton.sqlQueries.get(queryId);
		} else {
			throw new NextProtException("No SQL query found for " + queryId + " on a total of " + singleton.sqlQueries.size());
		}
	}

}
