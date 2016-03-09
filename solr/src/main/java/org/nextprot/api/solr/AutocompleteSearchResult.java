package org.nextprot.api.solr;

import java.util.*;

/**
 * Data transfer object that contains autocomplete search results
 */
public class AutocompleteSearchResult {

	private long elapsedTime;
	private String entity;
	private String index;
	private final Map<Long, Set<String>> autocomplete;

	public AutocompleteSearchResult() {
		autocomplete = new TreeMap<>(new Comparator<Long>() {
			@Override
			public int compare(Long l1, Long l2) {
				return l2.compareTo(l1);
			}
		});
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

	public Map<Long, Set<String>> getAutocomplete() {
		return autocomplete;
	}

	public void addResult(String name, long count) {

		if (!autocomplete.containsKey(count)) {
			autocomplete.put(count, new HashSet<String>());
		}

		autocomplete.get(count).add(name);
	}
}


