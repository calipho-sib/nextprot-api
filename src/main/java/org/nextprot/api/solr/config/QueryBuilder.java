package org.nextprot.api.solr.config;

import org.nextprot.api.solr.Query;


public interface QueryBuilder {

	/**
	 * It splits the query coming for the controller in tokens 
	 * and builds the query to Solr accordingly
	 * 
	 * @param query
	 * @return
	 */
	String buildQuery(Query query);
}
