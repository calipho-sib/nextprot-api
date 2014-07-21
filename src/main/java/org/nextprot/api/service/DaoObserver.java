package org.nextprot.api.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;


public interface DaoObserver {

	void notify(List<Map<String, Object>> rs) throws SolrServerException, IOException;
}
