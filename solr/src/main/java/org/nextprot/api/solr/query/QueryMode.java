package org.nextprot.api.solr.query;

public enum QueryMode {

	AUTOCOMPLETE,
	SIMPLE,
	ID_SEARCH,
	PROTEIN_LIST_SEARCH
	;

	private final String name;

	QueryMode() {
		this.name = name().toLowerCase();
	}

	public String getName() {
		return name;
	}
}
