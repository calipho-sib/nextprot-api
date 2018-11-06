package org.nextprot.api.solr.core;


import org.apache.solr.client.solrj.SolrServer;
import org.nextprot.api.solr.config.IndexConfiguration;

public interface SolrCore {

	/** @return the solr core name */
	String getName();

	/** @return the solr core url */
	String getUrl();

	/** @return the solr core entity */
	Entity getEntity();

	/** @return the solr core schema */
	SolrField[] getSchema();

	IndexConfiguration getDefaultConfig();
	IndexConfiguration getConfig(String configName);

	/** @return a new instance of a solr server */
	SolrServer newSolrServer();

	enum Entity {

		Entry,
		GoldEntry("gold-entry"),
		Term,
		Publication;

		private final String name;

		Entity() {
			this.name = name().toLowerCase();
		}

		Entity(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public static Entity valueOfName(String name) {

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
					throw new IllegalArgumentException("Unknown Entity."+name);
			}
		}
	}
}
