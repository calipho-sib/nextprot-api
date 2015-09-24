package org.nextprot.api.rdf.utils;

import java.util.Arrays;
import java.util.List;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.FilePatternDictionary;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

/**
 * Utility class that read SQL queries from classpath. 
 * Queries are hold in memory for improved performance
 * 
 * @author dteixeira
 */
@Repository
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

	/**
	 * Will return a list of sparql queries with tags
	 * @return
	 */
	public List<String> getSparqlWithTags() {
		throw new NextProtException("You need to implement this method");
	}


	@Override
	protected String getExtension() {
		return ".rq";
	}

	@Override
	protected String getLocation() {
		return "classpath*:sparql-queries/**/*.rq";
	}

	public List<String> getSparqlPrefixesList() {
		return Arrays.asList(getSparqlPrefixes().split("\n"));
	}

}
