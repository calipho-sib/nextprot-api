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

		String title = rawProps.get("title");

		dsq.setSparql(rawProps.get("sparql"));
		dsq.setTitle(title);
		dsq.setTags(getMetaInfo(rawContent).get("tags"));

		return dsq;

	}

	
	private String updateMapForLabelAndRemoveFromRawData(String p, String rawData, String label, Map<String, String> meta) {
		Matcher m = Pattern.compile(p, Pattern.DOTALL | Pattern.MULTILINE).matcher(rawData);
		if (m.find()) {
			meta.put(label, m.group(1));
			return rawData.replaceAll(p, "");
		}
		return rawData;
	}
	
	private Map<String, String> getMetaInfo(String rawData) {
		Map<String, String> meta = new HashMap<String, String>();
		String q = rawData;

		{ // get id and host

			String p = "#id:([^ ]+).?endpoint:([^\\n]*)";
			Matcher m = Pattern.compile(p, Pattern.DOTALL | Pattern.MULTILINE)
					.matcher(rawData);
			if (m.find()) {
				meta.put("id", m.group(1));
				meta.put("endpoint", m.group(2));
				q = q.replaceAll(p, "");
			}
		}

		{ // get tags
			String p = "[# ]?tags:([^ \\n]*)";
			Matcher m = Pattern.compile(p, Pattern.DOTALL | Pattern.MULTILINE)
					.matcher(rawData);
			if (m.find()) {
				meta.put("tags", m.group(1));
				q = q.replaceAll(p, "");
			}

		}

		{ // get acs
			String p = "[# ]?ac:([^ \\n]*)";
			Matcher m = Pattern.compile(p, Pattern.DOTALL | Pattern.MULTILINE)
					.matcher(rawData);
			if (m.find()) {
				meta.put("acs", m.group(1));
				q = q.replaceAll(p, "");
			}

		}

		{ // get count

			String p = "[# ]?count:([^\\n]*)";
			Matcher m = Pattern.compile(p, Pattern.DOTALL | Pattern.MULTILINE)
					.matcher(rawData);
			meta.put("count", "0");
			if (m.find()) {
				meta.put("count", m.group(1));
				q = q.replaceAll(p, "");
			}
		}

		{ // get count

			//
			// get title
			String p = "#title:([^\\n]*)";
			Matcher m = Pattern.compile(p, Pattern.DOTALL | Pattern.MULTILINE)
					.matcher(rawData);
			if (m.find()) {
				meta.put("title", m.group(1));
				q = q.replaceAll(p, "");
			}
		}
		
		meta.put("sparql", q.trim());

		return meta;
	}
}
