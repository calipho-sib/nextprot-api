package org.nextprot.api.solr.query;


import org.apache.solr.client.solrj.SolrQuery;
import org.nextprot.api.solr.core.SolrField;
import org.nextprot.api.solr.core.impl.settings.FieldConfigSet;
import org.nextprot.api.solr.core.impl.settings.SortConfig;

public interface QueryConfiguration<F extends SolrField> {

	void addConfigSet(FieldConfigSet<F> configSet);

	QueryMode getMode();

	/**
	 * It splits the query coming for the controller in tokens 
	 * and builds the query to Solr accordingly
	 * 
	 * @param query
	 * @return
	 */
	String formatQuery(Query<F> query);

	/**
	 * Builds a SOLR Query according to the specified index configuration
	 */
	SolrQuery convertQuery(Query<F> query) throws MissingSortConfigException;
	SolrQuery convertIdQuery(Query<F> query);

	class MissingSortConfigException extends Exception {

		public MissingSortConfigException(SortConfig.Criteria criteria, Query query) {

			super("sort config " + criteria + " does not exist: could not build solr query from " + query.toPrettyString());
		}
	}
}
