package org.nextprot.api.commons.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class that read SQL queries from a directory. $
 * Queries are hold in memory for improved performance
 * 
 * @author dteixeira
 */
public class SQLDictionary {
	
	private static final String SQL_PATH = "src/main/resources/sql/";
	private static SQLDictionary singleton = null;
	static {
		singleton = new SQLDictionary();
		singleton.initialize();
	}

	private Map<String, String> sqlQueries = null;

	private void initialize() {

		sqlQueries = new HashMap<String, String>();
		File f = new File(SQL_PATH);
		
		this.sqlQueries = findFiles(f, sqlQueries);
	}		

	
	private Map<String, String> findFiles(File file, Map<String, String> queryMap) {
		File[] subFiles = file.listFiles();
		
		for(File f : subFiles) {
			if(f.isDirectory())
				queryMap.putAll(findFiles(f, queryMap));
			else {
				queryMap.put(f.getName().replace(".sql",  ""), FileUtils.readFileAsString(f.getAbsolutePath()));
			}
		}
		return queryMap;
	}
	


	/**
	 * Gets the query
	 * 
	 * @param queryId
	 * @return
	 */
	public static String getSQLQuery(String queryId, Map<String,String> bindVariables) {
		String s = singleton.sqlQueries.get(queryId);
		for(String variableName : bindVariables.keySet()){
			s = s.replace(variableName, bindVariables.get(variableName));
		}
		return s;
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
