package org.nextprot.api.solr.indexation.service;

import org.apache.solr.common.SolrInputDocument;

public interface SolrEntryFieldCollectorService {

	SolrInputDocument buildSolrDoc(String entryAccession, boolean isGoldOnly);
}
