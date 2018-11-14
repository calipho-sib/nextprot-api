package org.nextprot.api.solr.core.impl.cores;

import org.nextprot.api.solr.indexation.SolrIndexationServer;
import org.nextprot.api.solr.query.SolrQueryServer;

public interface SolrServerNew extends SolrQueryServer, SolrIndexationServer {

	/** @return the base url */
	String getBaseURL();

	/** @return the complete url to solr core */
	String getURL();
}
