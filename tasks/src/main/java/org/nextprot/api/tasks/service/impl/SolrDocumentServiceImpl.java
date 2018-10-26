package org.nextprot.api.tasks.service.impl;

import org.apache.solr.common.SolrInputDocument;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.BeanDiscoveryService;
import org.nextprot.api.tasks.service.SolrDocumentService;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;
import org.springframework.beans.factory.annotation.Autowired;

public class SolrDocumentServiceImpl implements SolrDocumentService {

	@Autowired
	private BeanDiscoveryService beanDiscoveryService;

	@Override
	public SolrInputDocument solrDocument(Entry entry) {

		SolrInputDocument doc = new SolrInputDocument();

		for (FieldBuilder entryFieldBuilder : beanDiscoveryService.getAllBeans(FieldBuilder.class)) {

			// each builder should create index for a part of an entry
			// we should probably provide a collector such as a map of E
			entryFieldBuilder.initializeBuilder(entry);
		}


		return doc;
	}
}
