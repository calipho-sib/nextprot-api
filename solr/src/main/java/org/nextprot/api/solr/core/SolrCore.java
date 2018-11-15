package org.nextprot.api.solr.core;


import org.nextprot.api.solr.query.impl.config.IndexConfiguration;

public interface SolrCore {

	/** @return the solr core name */
	String getName();

	/** @return the solr core alias */
	Alias getAlias();

	/** @return the solr core schema */
	SolrField[] getSchema();

	IndexConfiguration getDefaultConfig();
	IndexConfiguration getConfig(String configName);

	/** @return a new instance of a solr client */
	SolrHttpClient newSolrClient();

	/** An alias to a SolCore instance */
	enum Alias {

		Entry(Entity.Entry),
		GoldEntry(Entity.Entry, "gold-entry"),
		Term(Entity.Term),
		Publication(Entity.Publication);

		private final Entity entity;
		private final String name;

		Alias(Entity entity) {
			this(entity, null);
		}

		Alias(Entity entity, String name) {
			this.entity = entity;
			this.name = (name != null) ? name.toLowerCase() : name().toLowerCase();
		}

		public String getName() {
			return name;
		}

		public Entity getEntity() {
			return entity;
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

		public static Alias fromEntityAndQuality(Entity entity, String quality) {

			return ((entity == Entity.Entry) && (quality != null && quality.equalsIgnoreCase("gold"))) ?
					SolrCore.Alias.GoldEntry : SolrCore.Alias.valueOfName(entity.getName());
		}
	}
}
