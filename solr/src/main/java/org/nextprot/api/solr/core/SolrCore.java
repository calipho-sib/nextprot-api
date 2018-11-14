package org.nextprot.api.solr.core;


import org.nextprot.api.solr.query.impl.config.IndexConfiguration;

public interface SolrCore {

	/** @return the solr core name */
	String getName();

	/** @return the solr core url */
	String getUrl();

	/** @return the solr core alias */
	Alias getAlias();

	/** @return the solr core schema */
	SolrField[] getSchema();

	IndexConfiguration getDefaultConfig();
	IndexConfiguration getConfig(String configName);

	/** @return a new instance of a solr query server */
	SolrServer newSolrServer();

	/** An alias to a SolCore instance */
	enum Alias {

		Entry,
		GoldEntry("gold-entry"),
		Term,
		Publication;

		private final String name;

		Alias() {
			this.name = name().toLowerCase();
		}

		Alias(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public static Alias valueOfName(String name) {

			switch (name) {
				case "entry":
					return Entry;
				case "gold-entry":
					return GoldEntry;
				case "term":
					return Term;
				case "publication":
					return Publication;
				default:
					throw new IllegalArgumentException("Unknown Alias."+name);
			}
		}
	}
}
