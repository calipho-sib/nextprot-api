package org.nextprot.api.solr.core;


import org.apache.solr.client.solrj.SolrQuery;
import org.nextprot.api.solr.core.impl.config.FieldConfigSet;
import org.nextprot.api.solr.core.impl.config.SortConfig;
import org.nextprot.api.solr.query.Query;

public interface QueryConfiguration<F extends SolrField> {

	void addConfigSet(FieldConfigSet<F> configSet);

	SearchMode getMode();

	/**
	 * It splits the query coming for the controller in tokens 
	 * and builds the query to Solr accordingly
	 * 
	 * @param query
	 * @return
	 */
	String formatQuery(Query query);

	/**
	 * Builds a SOLR Query according to the specified index configuration
	 */
	SolrQuery convertQuery(Query query) throws MissingSortConfigException;
	SolrQuery convertIdQuery(Query query);

	class MissingSortConfigException extends Exception {

		public MissingSortConfigException(SortConfig.Criteria criteria, Query query) {

			super("sort config " + criteria + " does not exist: could not build solr query from " + query.toPrettyString());
		}
	}
}
