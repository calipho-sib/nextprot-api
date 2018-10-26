package org.nextprot.api.tasks.service;

import org.apache.solr.common.SolrInputDocument;
import org.nextprot.api.core.domain.Entry;

public interface SolrDocumentService {

	SolrInputDocument solrDocument(Entry entry);
}
