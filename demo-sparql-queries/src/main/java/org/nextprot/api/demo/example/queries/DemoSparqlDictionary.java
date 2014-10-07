package org.nextprot.api.demo.example.queries;


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
public class DemoSparqlDictionary extends FilePatternDictionary {

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
		return "classpath*:demo-sparql-queries/**/*.rq";
	}

	
	public String getDemoQuery(String queryId) {
		return super.getResource(queryId);
	}

}
