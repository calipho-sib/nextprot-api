package org.nextprot.api.solr;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.nextprot.utils.Pair;

/**
 * Encapsulate Solr result. 
 * Enables us to take what we need for the initial Solr class
 * 
 * @author mpereira
 *
 */
public class SearchResult  {
	private long elapsedTime;
	private String entity;
	private String index;
	private float maxScore;
	private long numFound;
	private int start;
	private int rows;
	private List<SearchResultItem> results;
	private Map<String, SearchResultFacet> facets;
	
	private SearchResultSpellcheck spellcheck;
	
	public SearchResult() { 
		this.results = new ArrayList<SearchResultItem>();
		this.facets = new HashMap<String, SearchResult.SearchResultFacet>();
	}
	
	public SearchResult(String entity, String index) {
		this();
		this.entity = entity;
		this.index = index;
	}
	
	public SearchResult(long elapsedTime, List<SearchResultItem> results) {
		this.elapsedTime = elapsedTime;
		this.results = results;
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

	public float getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(float score) {
		this.maxScore = score;
	}
	
	public long getNumFound() {
		return this.numFound;
	}

	public void setNumFound(long numFound) {
		this.numFound = numFound;
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

	public List<SearchResultItem> getResults() {
		return results;
	}

	public void setResults(List<SearchResultItem> results) {
		this.results = results;
	}

	public void addSearchResultFacet(SearchResultFacet facet) {
		if(this.facets == null) 
			this.facets = new HashMap<String, SearchResult.SearchResultFacet>();
		this.facets.put(facet.getName(), facet);
	}
	
	public Map<String, SearchResultFacet> getFacets() {
		return this.facets;
	}
	
	public SearchResultFacet getFacet(String name) {
		if(this.facets.containsKey(name))
			return this.facets.get(name);
		return null;
	}
	
	public void setSpellCheck(SearchResultSpellcheck spellcheck) {
		this.spellcheck = spellcheck; 
	}

	public SearchResultSpellcheck getSpellcheck() {
		return this.spellcheck;
	}
	
	public String toString() {
		return "results: "+this.results.size()+" facets: "+this.facets.size();
	}
	
	/**
	 * Represents one item in the Solr results
	 *  
	 * @author mpereira
	 *
	 */
	public static class SearchResultItem {
		private Map<String, Object> properties;
		
		public SearchResultItem() { 
			this.properties = new HashMap<String, Object>(); 
		}
		
		public void addProperty(String name, Object value) {
			this.properties.put(name, value);
		}

		public Map<String, Object> getProperties() {
			return properties;
		}

		public void setProperties(Map<String, Object> properties) {
			this.properties = properties;
		}
		
	}
	
	public static class SearchResultFacet {
		private String name;
		private List<Pair<String, Long>> facetFields;
		
		public SearchResultFacet(String name) {
			this.name = name;
			this.facetFields = new ArrayList<Pair<String,Long>>();
		}
		
		public String getName() {
			return this.name;
		}
		
		public void addFacetField(String fieldName, Long fieldCount) {
			this.facetFields.add(Pair.create(fieldName, fieldCount));
		}

		public List<Pair<String, Long>> getFacetFields() {
			return facetFields;
		}
		
		public List<Pair<String, Long>> getFoundFacetFields() {
			List<Pair<String, Long>> foundFields = new ArrayList<Pair<String, Long>>();
			for(Pair<String, Long> p : this.facetFields)
				if(p.getSecond() > 0) 
					foundFields.add(p);
			
			System.out.println("FOUND: "+ foundFields.size());
			return foundFields;
		}
		
	}
	
	public static class SearchResultSpellcheck {
		private SortedSet<Pair<String, Long>> collations = new TreeSet<Pair<String, Long>>(new CollationComparator());
		private Map<String, List<String>> suggestions = new HashMap<String, List<String>>();
		
		public void addCollation(String collation, long hits) {
			this.collations.add(Pair.create(collation, hits));
		}
		
		public void addSuggestions(String token, List<String> alternatives) {
			this.suggestions.put(token, alternatives);
		}

		public Set<Pair<String, Long>> getCollations() {
			return collations;
		}

		public Map<String, List<String>> getSuggestions() {
			return suggestions;
		}



		class CollationComparator implements Comparator<Pair<String, Long>> {

			public int compare(Pair<String, Long> o1, Pair<String, Long> o2) {
				return o2.getSecond().compareTo(o1.getSecond());
			}
		}
	}
}


