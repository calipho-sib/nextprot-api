package org.nextprot.api.solr.core;

import org.nextprot.api.solr.indexation.SolrIndexationClient;
import org.nextprot.api.solr.query.SolrQueryClient;

public interface SolrHttpClient extends SolrQueryClient, SolrIndexationClient {

	/** @return the base url */
	String getBaseURL();

	/** @return the complete url to solr core */
	String getURL();
}
