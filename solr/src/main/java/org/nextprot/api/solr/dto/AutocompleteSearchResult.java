package org.nextprot.api.solr.dto;

import org.apache.commons.collections.map.HashedMap;

import java.util.Map;

/**
 * Data transfer object that contains autocomplete search results
 */
public class AutocompleteSearchResult {

	private long elapsedTime;
	private String entity;
	private String index;
	private final Map<String, Integer> autocomplete;

	public AutocompleteSearchResult() {
		autocomplete = new HashedMap();
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

	public Map<String, Integer> getAutocomplete() {
		return autocomplete;
	}

	public void addResult(String name, int count) {

		autocomplete.put(name, count);
	}
}


