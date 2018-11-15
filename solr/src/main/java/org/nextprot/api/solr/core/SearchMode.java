package org.nextprot.api.solr.core;

public enum SearchMode {

	AUTOCOMPLETE,
	SIMPLE,
	ID_SEARCH,
	PL_SEARCH
	;

	private final String name;

	SearchMode() {
		this.name = name().toLowerCase();
	}

	public String getName() {
		return name;
	}
}
