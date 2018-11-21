package org.nextprot.api.solr.query;

public enum QueryMode {

	AUTOCOMPLETE,
	SIMPLE,
	ID_SEARCH,
	PL_SEARCH
	;

	private final String name;

	QueryMode() {
		this.name = name().toLowerCase();
	}

	public String getName() {
		return name;
	}
}
