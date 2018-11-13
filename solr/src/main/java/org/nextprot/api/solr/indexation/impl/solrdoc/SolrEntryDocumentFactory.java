package org.nextprot.api.solr.indexation.impl.solrdoc;

import com.google.common.base.Preconditions;
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

public class SolrEntryDocumentFactory implements SolrDocumentFactory {

	private final String entryAccession;
    private final boolean isGold;

	public SolrEntryDocumentFactory(String entryAccession, boolean isGold) {

		Preconditions.checkNotNull(entryAccession, "entry accession should not be undefined");
		Preconditions.checkArgument(!entryAccession.isEmpty(), "entry accession should not be empty");

		this.entryAccession = entryAccession;
        this.isGold = isGold;
    }

    // TODO: remove this code once the newer is validated
	private SolrInputDocument createSolrInputDocumentOld() {

		Map<EntrySolrField, EntrySolrFieldCollector> collectors = mapCollectorsByEntryField();

		Preconditions.checkArgument(!collectors.isEmpty(),
				"Services are missing (check that spring config 'solr-context.xml' has been correctly imported)");

		SolrInputDocument doc = new SolrInputDocument();

		for (EntrySolrField esf : EntrySolrField.values()) {
			if (esf == EntrySolrField.TEXT || esf == EntrySolrField.SCORE) {
				continue; // Directly computed by SOLR
			}

			EntrySolrFieldCollector entrySolrFieldCollector = collectors.get(esf);

			Map<EntrySolrField, Object> fields = new HashMap<>();

			// TODO: should give SolrInputDocument instead of a map
			entrySolrFieldCollector.collect(fields, entryAccession, isGold);

			fields.keySet().forEach(entrySolrField -> doc.addField(entrySolrField.getName(), fields.get(entrySolrField)));
		}

		return doc;
	}

	@Override
	public SolrInputDocument createSolrInputDocument() {

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

	static Map<EntrySolrField, EntrySolrFieldCollector> mapCollectorsByEntryField() {

		Map<EntrySolrField, EntrySolrFieldCollector> fieldsBuilderMap = new HashMap<>();

		for (EntrySolrFieldCollector collector : SpringApplicationContext.getAllBeansOfType(EntrySolrFieldCollector.class)) {
			if (!collector.getCollectedFields().isEmpty()) {
				for (EntrySolrField indexedField : collector.getCollectedFields()) {
					NPreconditions.checkTrue(!(fieldsBuilderMap.containsKey(indexedField)), "The field " + indexedField.getName() + " cannot be indexed by several builders: " + indexedField.getClass() + ", " + fieldsBuilderMap.get(indexedField));
					fieldsBuilderMap.put(indexedField, collector);
				}
			}

		}
		return fieldsBuilderMap;
	}
}
