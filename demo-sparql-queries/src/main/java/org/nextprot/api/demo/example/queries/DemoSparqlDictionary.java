package org.nextprot.api.demo.example.queries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.FilePatternDictionary;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

/**
 * Utility class that read SQL queries from classpath. Queries are hold in
 * memory for improved performance
 * 
 * @author dteixeira
 */
@Repository
@Lazy
public class DemoSparqlDictionary extends FilePatternDictionary {

	/**
	 * Will return a list of sparql queries with tags
	 * 
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

	public DemoSparqlQuery getDemoQuery(String queryId) {
		String sparqlRaw = super.getResource(queryId);
		return buildSparqlQueryFromRawContent(sparqlRaw);

	}

	public List<DemoSparqlQuery> getDemoSparqlList() {
		Collection<String> rawData = super.getResourcesMap().values();
		List<DemoSparqlQuery> demoSparqlQueriesList = new ArrayList<DemoSparqlQuery>();

		for (String raw : rawData) {
			demoSparqlQueriesList.add(buildSparqlQueryFromRawContent(raw));
		}

		return demoSparqlQueriesList;

	}

	private DemoSparqlQuery buildSparqlQueryFromRawContent(String rawContent) {
		DemoSparqlQuery dsq = new DemoSparqlQuery();
		Map<String, String> rawProps = getMetaInfo(rawContent);

		dsq.setSparql(rawProps.get("sparql"));
		dsq.setTitle(rawProps.get("title"));
		dsq.setTags(rawProps.get("tags"));
		dsq.setAcs(rawProps.get("acs"));

		return dsq;

	}

	
	private String parseAndGlupRawQuery(String rawData, String q, String label, Map<String, String> meta) {

		String p = "[# ]?" + label + ":([^\\n]*)";
		Matcher m = Pattern.compile(p, Pattern.DOTALL | Pattern.MULTILINE).matcher(rawData);
		if (m.find()) {
			meta.put(label, m.group(1));
			return q.replaceAll(p, "");
		}
		return q;
	}
	
	private Map<String, String> getMetaInfo(String rawData) {
		Map<String, String> meta = new HashMap<String, String>();
		String q = rawData;

		q = parseAndGlupRawQuery(rawData, q, "id", meta);
		q = parseAndGlupRawQuery(rawData, q, "endpoint", meta);
		q = parseAndGlupRawQuery(rawData, q, "tags", meta);
		q = parseAndGlupRawQuery(rawData, q, "ac", meta);
		q = parseAndGlupRawQuery(rawData, q, "count", meta);
		q = parseAndGlupRawQuery(rawData, q, "title", meta);
		
		meta.put("sparql", q.trim());

		return meta;
	}
}
