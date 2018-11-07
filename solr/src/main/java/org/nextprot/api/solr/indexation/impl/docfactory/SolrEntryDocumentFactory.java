package org.nextprot.api.solr.indexation.impl.docfactory;

import org.apache.solr.common.SolrInputDocument;
import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.commons.utils.SpringApplicationContext;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.nextprot.api.solr.indexation.impl.docfactory.entryfieldcollector.EntrySolrFieldCollector;

import java.util.HashMap;
import java.util.Map;

public class SolrEntryDocumentFactory extends SolrDocumentBaseFactory<Entry> {

    private final boolean isGold;

	public SolrEntryDocumentFactory(Entry entry, boolean isGold) {
        super(entry);
        this.isGold = isGold;
    }

	@Override
	public SolrInputDocument createSolrInputDocument() {

		Map<EntrySolrField, EntrySolrFieldCollector> fieldsBuilderMap = mapCollectorsByEntryField();

		SolrInputDocument doc = new SolrInputDocument();

		for (EntrySolrField f : EntrySolrField.values()) {
			if (f == EntrySolrField.TEXT || f == EntrySolrField.SCORE) {
				continue; // Directly computed by SOLR
			}

			EntrySolrFieldCollector entrySolrFieldCollector = fieldsBuilderMap.get(f);
			entrySolrFieldCollector.collect(solrizableObject, isGold);

			doc.addField(f.getName(), entrySolrFieldCollector.getFieldValue(f));
		}

		//Reset all fields builders
		for (EntrySolrField f : EntrySolrField.values()) {
			if (f == EntrySolrField.TEXT || f == EntrySolrField.SCORE) {
				continue; // Directly computed by SOLR
			}
			fieldsBuilderMap.get(f).clear();
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
