package org.nextprot.api.solr.core;

public enum Entity {

	Entry,
	Term,
	Publication;

	private final String name;

	Entity() {
		this.name = name().toLowerCase();
	}

	public String getName() {
		return name;
	}

	public static Entity valueOfName(String name) {

		switch (name) {
			case "entry":
				return Entry;
			case "term":
				return Term;
			case "publication":
				return Publication;
			default:
				throw new IllegalArgumentException("Unknown enum Entity."+name);
		}
	}
}
