package org.nextprot.api.solr.indexation.service;

import org.apache.solr.common.SolrInputDocument;
import org.nextprot.api.core.domain.Entry;

/**
 * Collect associated index field data from a neXtProt domain entry
 *
 * TODO: should refactor all services in package solrdoc.entrydoc that should not be mutable!
 */
public interface SolrEntryFieldCollectorService {

	void collectSolrFields(SolrInputDocument solrInputDocument, Entry entry, boolean isGoldOnly);
}
