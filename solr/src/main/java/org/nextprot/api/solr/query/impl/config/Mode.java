package org.nextprot.api.solr.query.impl.config;

public enum Mode {

	AUTOCOMPLETE,
	SIMPLE,
	ID_SEARCH,
	PL_SEARCH
	;

	private final String name;

	Mode() {
		this.name = name().toLowerCase();
	}

	public String getName() {
		return name;
	}
}
