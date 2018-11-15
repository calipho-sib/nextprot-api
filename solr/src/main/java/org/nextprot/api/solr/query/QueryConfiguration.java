package org.nextprot.api.solr.query;


import org.apache.solr.client.solrj.SolrQuery;

public interface QueryConfiguration {

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
	SolrQuery convertQuery(Query query) throws BuildSolrQueryException;
	SolrQuery convertIdQuery(Query query);

	class BuildSolrQueryException extends Exception {

		public BuildSolrQueryException(String message, Query query) {

			super(message + ": could not build solr query from " + query.toPrettyString());
		}
	}
}
