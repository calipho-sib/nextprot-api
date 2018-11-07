package org.nextprot.api.solr.index.docfactory;

import org.apache.solr.common.SolrInputDocument;
import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.commons.utils.SpringApplicationContext;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.core.EntrySolrField;
import org.nextprot.api.solr.index.docfactory.entryfield.EntrySolrFieldCollector;

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

		Map<EntrySolrField, EntrySolrFieldCollector> fieldsBuilderMap = mapBuildersByEntryField();

		SolrInputDocument doc = new SolrInputDocument();

		for (EntrySolrField f : EntrySolrField.values()) {
			if (f == EntrySolrField.TEXT || f == EntrySolrField.SCORE) {
				continue; // Directly computed by SOLR
			}

			EntrySolrFieldCollector entrySolrFieldCollector = fieldsBuilderMap.get(f);
			entrySolrFieldCollector.collect(solrizableObject, isGold);

			Object o = entrySolrFieldCollector.getFieldValue(f, f.getType());
			doc.addField(f.getName(), o);
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

	public static Map<EntrySolrField, EntrySolrFieldCollector> mapBuildersByEntryField() {

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
