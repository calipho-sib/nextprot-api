package org.nextprot.api.solr.indexation.solrdoc;

import org.apache.solr.common.SolrInputDocument;
import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.commons.utils.SpringApplicationContext;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.core.EntrySolrField;
import org.nextprot.api.solr.indexation.solrdoc.entrydoc.EntrySolrFieldCollector;

import java.util.HashMap;
import java.util.Map;

public class SolrEntryDocumentFactory extends SolrDocumentFactory<Entry> {

    private final boolean isGold;

	public SolrEntryDocumentFactory(Entry entry, boolean isGold) {
        super(entry);
        this.isGold = isGold;
    }

	@Override
	public SolrInputDocument calcSolrInputDocument() {

		Map<EntrySolrField, EntrySolrFieldCollector> collectors = mapCollectorsByEntryField();

		SolrInputDocument doc = new SolrInputDocument();

		for (EntrySolrField f : EntrySolrField.values()) {
			if (f == EntrySolrField.TEXT || f == EntrySolrField.SCORE) {
				continue; // Directly computed by SOLR
			}

			EntrySolrFieldCollector entrySolrFieldCollector = collectors.get(f);

			Map<EntrySolrField, Object> fields = new HashMap<>();

			// TODO: should give SolrInputDocument instead of a map
			entrySolrFieldCollector.collect(fields, solrizableObject, isGold);

			doc.addField(f.getName(), fields.get(f));
		}

		return doc;
	}

	public static Map<EntrySolrField, EntrySolrFieldCollector> mapCollectorsByEntryField() {

		Map<EntrySolrField, EntrySolrFieldCollector> fieldsBuilderMap = new HashMap<>();

		for (EntrySolrFieldCollector builder : SpringApplicationContext.getAllBeansOfType(EntrySolrFieldCollector.class)) {
			if (!builder.getCollectedFields().isEmpty()) {
				for (EntrySolrField indexedField : builder.getCollectedFields()) {
					NPreconditions.checkTrue(!(fieldsBuilderMap.containsKey(indexedField)), "The field " + indexedField.getName() + " cannot be indexed by several builders: " + indexedField.getClass() + ", " + fieldsBuilderMap.get(indexedField));
					fieldsBuilderMap.put(indexedField, builder);
				}
			}

		}
		return fieldsBuilderMap;
	}
}
