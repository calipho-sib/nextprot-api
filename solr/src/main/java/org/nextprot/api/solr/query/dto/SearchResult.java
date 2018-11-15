package org.nextprot.api.solr.query.dto;

import org.nextprot.api.commons.utils.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Encapsulate Solr result. 
 * Enables us to take what we need for the initial Solr class
 * 
 * @author mpereira
 * @author fnikitin
 *
 */
public class SearchResult  {

	private long elapsedTime;
	private String entity;
	private String index;
	private float score;
	private long found;
	private int start;
	private int rows;
	private List<Map<String, Object>> results;
	private Map<String, List<Map<String, Object>>> facets;
	private Map<String, Object> spellcheck;
	
	public SearchResult() { 
		this.results = new ArrayList<>();
		this.facets = new HashMap<>();
	}
	
	public SearchResult(String entity, String index) {
		this();
		this.entity = entity;
		this.index = index;
	}
	
	public long getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}
	
	public long getFound() {
		return this.found;
	}

	public void setFound(long found) {
		this.found = found;
	}

	public int getStart() {
		return this.start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public List<Map<String, Object>> getResults() {
		return results;
	}

	public void addAllResults(List<Map<String, Object>> results) {

		this.results.addAll(results);
	}

	public void addSearchResultFacet(Facet facet) {
		if(this.facets == null)
			this.facets = new HashMap<>();

		this.facets.put(facet.getName(), facet.getFacetFields());
	}

	public Map<String, List<Map<String, Object>>> getFacets() {
		return this.facets;
	}

	public List<Map<String, Object>> getFilters() {
		return getFacet("filters");
	}

	public List<Map<String, Object>> getFacet(String name) {
		if(this.facets.containsKey(name))
			return this.facets.get(name);
		return new ArrayList<>();
	}

	public List<Map<String, Object>> getFoundFacets(String name) {
		List<Map<String, Object>> found = new ArrayList<>();

		for(Map<String, Object> p : getFacet(name))
			if((Long)p.get(Facet.FIELD_COUNT) > 0)
				found.add(p);

		return found;
	}
	
	public void setSpellCheck(Spellcheck spellcheck) {
		this.spellcheck = spellcheck.getContent();
	}

	public Map<String, Object> getSpellcheck() {

		if (spellcheck == null)
			return Spellcheck.newEmptyMap();

		return spellcheck;
	}

	public Set<Map<String, Object>> getCollations() {

		if (spellcheck != null && spellcheck.containsKey(Spellcheck.COLLATIONS))
			return (Set<Map<String, Object>>) spellcheck.get(Spellcheck.COLLATIONS);
		return new HashSet<>();
	}

	public Map<String, List<String>> getSuggestions() {

		if (spellcheck != null && spellcheck.containsKey(Spellcheck.SUGGESTIONS))
			return (Map<String, List<String>>) spellcheck.get(Spellcheck.SUGGESTIONS);
		return new HashMap<>();
	}

	public String toString() {
		return "results: "+this.results.size()+" facets: "+this.facets.size();
	}
	
	public static class Facet {
		public static final String FIELD_NAME = "name";
		public static final String FIELD_COUNT = "count";

		private String name;
		private List<Map<String, Object>> facetFields;
		
		public Facet(String name) {
			this.name = name;
			this.facetFields = new ArrayList<>();
		}
		
		public String getName() {
			return this.name;
		}
		
		public void addFacetField(String fieldName, long fieldCount) {
			Map<String, Object> map = new HashMap<>();
			map.put(FIELD_NAME, fieldName);
			map.put(FIELD_COUNT, fieldCount);
			facetFields.add(map);
		}

		public List<Map<String, Object>> getFacetFields() {
			return facetFields;
		}
	}

	public static class Spellcheck {

		private static final String COLLATIONS = "collations";
		private static final String SUGGESTIONS = "suggestions";
		public static final String COLLATION_QUERY = "query";
		public static final String COLLATION_HITS = "hits";

		private Map<String, Object> content = new HashMap<>();

		private Set<Map<String, Object>> collations = new TreeSet<>(new CollationComparator());
		private Map<String, List<String>> suggestions = new HashMap<>();

		public Spellcheck() {

			content.put(COLLATIONS, collations);
			content.put(SUGGESTIONS, suggestions);
		}

		public void addCollation(String collation, long hits) {

			Map<String, Object> hitsMap = new HashMap<>();
			hitsMap.put(COLLATION_QUERY, StringUtils.removePlus(collation));
			hitsMap.put(COLLATION_HITS, hits);

			this.collations.add(hitsMap);
		}

		public static Map<String, Object> newEmptyMap() {

			Map<String, Object> map = new HashMap<>();

			map.put(COLLATIONS, new HashSet<>());
			map.put(SUGGESTIONS, new HashMap<>());

			return map;
		}

		public Map<String, Object> getContent() {
			return content;
		}

		Set<Map<String, Object>> getCollations() {
			return collations;
		}

		public void addSuggestions(String token, List<String> alternatives) {
			this.suggestions.put(token, alternatives);
		}

		class CollationComparator implements Comparator<Map<String, Object>> {

			public int compare(Map<String, Object> m1, Map<String, Object> m2) {

				return ((Long)m2.get(COLLATION_HITS)).compareTo((Long)m1.get(COLLATION_HITS));
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SearchResult that = (SearchResult) o;
		return found == that.found &&
				start == that.start &&
				rows == that.rows &&
				Objects.equals(entity, that.entity) &&
				Objects.equals(index, that.index) &&
				Objects.equals(results, that.results) &&
				Objects.equals(facets, that.facets) &&
				Objects.equals(spellcheck, that.spellcheck);
	}

	@Override
	public int hashCode() {
		return Objects.hash(entity, index, found, start, rows, results, facets, spellcheck);
	}
}


