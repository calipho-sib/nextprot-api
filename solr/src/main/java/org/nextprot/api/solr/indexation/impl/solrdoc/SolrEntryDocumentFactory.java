package org.nextprot.api.solr.indexation.impl.solrdoc;

import org.apache.solr.common.SolrInputDocument;
import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.commons.utils.SpringApplicationContext;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.nextprot.api.solr.indexation.SolrDocumentFactory;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.EntrySolrFieldCollector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SolrEntryDocumentFactory implements SolrDocumentFactory<String> {

    private final boolean isGold;

	public SolrEntryDocumentFactory(boolean isGold) {

        this.isGold = isGold;
    }

	@Override
	public SolrInputDocument createSolrInputDocument(String entryAccession) {

		NPreconditions.checkNotNull(entryAccession, "unable to create a solr doc from an undefined entry accession");
		NPreconditions.checkTrue(!entryAccession.isEmpty(), "unable to create a solr doc from an empty entry accession");

		SolrInputDocument solrInputDocument = new SolrInputDocument();

		Map<EntrySolrField, Object> fields = new HashMap<>();

		// 1. collect everything from all collectors
		for (EntrySolrFieldCollector collector : getCollectors()) {

			collector.collect(fields, entryAccession, isGold);
		}

		// 2. set solrInputDocument with map
		fields.keySet().forEach(esf -> solrInputDocument.addField(esf.getName(), fields.get(esf)));

		return solrInputDocument;

	}

	static Collection<EntrySolrFieldCollector> getCollectors() {

		Collection<EntrySolrFieldCollector> collectors = new ArrayList<>();

		Set<EntrySolrField> fields = new HashSet<>();

		for (EntrySolrFieldCollector collector : SpringApplicationContext.getAllBeansOfType(EntrySolrFieldCollector.class)) {
			if (!collector.getCollectedFields().isEmpty()) {
				for (EntrySolrField indexedField : collector.getCollectedFields()) {

					NPreconditions.checkTrue(!(fields.contains(indexedField)), "The field " + indexedField.getName() +
							" cannot be handled by several collectors: " + indexedField.getClass() + ", " + collector);

					fields.add(indexedField);
				}
				collectors.add(collector);
			}

		}
		return collectors;
	}
}
