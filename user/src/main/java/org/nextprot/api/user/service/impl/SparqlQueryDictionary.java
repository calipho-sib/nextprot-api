package org.nextprot.api.user.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.FilePatternDictionary;
import org.nextprot.api.user.domain.UserQuery;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Sets;

/**
 * Utility class that read SPARQL queries from classpath. Queries are hold in
 * memory for improved performance
 * 
 * @author dteixeira
 */
@Repository
@Lazy
public class SparqlQueryDictionary extends FilePatternDictionary {


	@Override
	protected String getExtension() {
		return ".rq";
	}

	@Override
	protected String getLocation() {
		return "classpath*:nextprot-queries/**/*.rq";
	}

	public UserQuery getSparqlQuery(String queryId) {
		String sparqlRaw = super.getResource(queryId);
		return buildSparqlQueryFromRawContent(sparqlRaw);

	}

	public synchronized List<UserQuery> getSparqlQueryList() {
		Collection<String> rawData = super.getResourcesMap().values();
		List<UserQuery> demoSparqlQueriesList = new ArrayList<UserQuery>();

		for (String raw : rawData) {
			demoSparqlQueriesList.add(buildSparqlQueryFromRawContent(raw));
		}

		return demoSparqlQueriesList;

	}

	private UserQuery buildSparqlQueryFromRawContent(String rawContent) {
		UserQuery dsq = new UserQuery();
		Map<String, String> rawProps = getMetaInfo(rawContent);

		dsq.setSparql(rawProps.get("query"));
		dsq.setTitle(rawProps.get("title"));
		dsq.setOwner("nextprot");
		dsq.setOwnerId(-1);
		dsq.setDescription(rawProps.get("comment"));

		try {
			dsq.setPublicId(rawProps.get("id"));
			dsq.setUserQueryId(Long.valueOf(rawProps.get("id").replaceAll("NXQ_", "")));
		}catch(Exception e){
			dsq.setUserQueryId(0);
		}
		
		if(rawProps.get("tags") != null){
			dsq.setTags(Sets.newHashSet(rawProps.get("tags").split(",")));
		} else dsq.setTags(new HashSet<String>());
		
		//dsq.set(rawProps.get("count"));
		//dsq.setAcs(rawProps.get("acs"));

		return dsq;

	}

	
	private String parseAndGlupRawQuery(String rawData, String q, String label, Map<String, String> meta) {

		String p = "[# ]?" + label + ":([^\\n]*)";
		Matcher m = Pattern.compile(p, Pattern.DOTALL | Pattern.MULTILINE).matcher(rawData);
		boolean found = false;
		while (m.find()) { //
			found = true;
			if(!meta.containsKey(label)){
				meta.put(label, m.group(1));
			}else {
				meta.put(label, meta.get(label) + "\n" + m.group(1));
			}
			
		}
		if(found) return q.replaceAll(p, "");

		return q;
	}
	
	private Map<String, String> getMetaInfo(String rawData) {
		Map<String, String> meta = new HashMap<String, String>();
		String q = rawData;

		q = parseAndGlupRawQuery(rawData, q, "id", meta);
		q = parseAndGlupRawQuery(rawData, q, "endpoint", meta);
		q = parseAndGlupRawQuery(rawData, q, "tags", meta);
		q = parseAndGlupRawQuery(rawData, q, "acs", meta);
		q = parseAndGlupRawQuery(rawData, q, "count", meta);
		q = parseAndGlupRawQuery(rawData, q, "title", meta);
		q = parseAndGlupRawQuery(rawData, q, "comment", meta);
		q = parseAndGlupRawQuery(rawData, q, "time", meta);
		
		meta.put("query", q.trim());

		return meta;
	}
	
	public void reloadDemoQueries() {
		super.loadResources();
	}
}
