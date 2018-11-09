package org.nextprot.api.solr.indexation.impl.solrdoc;

import com.google.common.base.Preconditions;
import org.apache.solr.common.SolrInputDocument;
import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.commons.utils.SpringApplicationContext;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.EntrySolrFieldCollector;

import java.util.HashMap;
import java.util.Map;

public class SolrEntryDocumentFactory implements org.nextprot.api.solr.indexation.SolrDocumentFactory {

	private final String entryAccession;
    private final boolean isGold;

	public SolrEntryDocumentFactory(String entryAccession, boolean isGold) {

		Preconditions.checkNotNull(entryAccession, "entry accession shoud not be undefined");
		Preconditions.checkArgument(!entryAccession.isEmpty(), "entry accession shoud not be empty");

		this.entryAccession = entryAccession;
        this.isGold = isGold;
    }

	@Override
	public SolrInputDocument createSolrInputDocument() {

		Map<EntrySolrField, EntrySolrFieldCollector> collectors = mapCollectorsByEntryField();

		SolrInputDocument doc = new SolrInputDocument();

		for (EntrySolrField f : EntrySolrField.values()) {
			if (f == EntrySolrField.TEXT || f == EntrySolrField.SCORE) {
				continue; // Directly computed by SOLR
			}

			EntrySolrFieldCollector entrySolrFieldCollector = collectors.get(f);

			Map<EntrySolrField, Object> fields = new HashMap<>();

			// TODO: should give SolrInputDocument instead of a map
			entrySolrFieldCollector.collect(fields, entryAccession, isGold);

			doc.addField(f.getName(), fields.get(f));
		}

		return doc;
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
