package org.nextprot.api.solr.core;

import org.nextprot.api.solr.indexation.SolrIndexationClient;
import org.nextprot.api.solr.query.SolrQueryClient;

public interface SolrHttpClient extends SolrQueryClient, SolrIndexationClient {

	/** @return The URL of the Solr server. For example, "
	 *          <code>http://localhost:8983/solr</code>". */
	String getBaseURL();

	/** @return the complete URL to the solr core. For example, "
	 *          <code>http://localhost:8983/solr/#/npcvs1</code>"*/
	String getURL();
}
