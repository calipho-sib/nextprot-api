package org.nextprot.api.solr;


import org.nextprot.api.solr.dto.Query;

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
