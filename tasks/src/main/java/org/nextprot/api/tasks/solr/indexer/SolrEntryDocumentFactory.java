package org.nextprot.api.tasks.solr.indexer;

import org.apache.solr.common.SolrInputDocument;
import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.commons.utils.SpringApplicationContext;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.index.EntryField;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;

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

		Map<EntryField, EntryFieldBuilder> fieldsBuilderMap = mapBuildersByEntryField();

		SolrInputDocument doc = new SolrInputDocument();

		for (EntryField f : EntryField.values()) {
			if (f == EntryField.TEXT || f == EntryField.SCORE) {
				continue; // Directly computed by SOLR
			}

			EntryFieldBuilder entryFieldBuilder = fieldsBuilderMap.get(f);
			entryFieldBuilder.collect(solrizableObject, isGold);

			Object o = entryFieldBuilder.getFieldValue(f, f.getClazz());
			doc.addField(f.getName(), o);
		}

		//Reset all fields builders
		for (EntryField f : EntryField.values()) {
			if (f == EntryField.TEXT || f == EntryField.SCORE) {
				continue; // Directly computed by SOLR
			}
			fieldsBuilderMap.get(f).reset();
		}

		return doc;
	}

	static Map<EntryField, EntryFieldBuilder> mapBuildersByEntryField() {

		Map<EntryField, EntryFieldBuilder> fieldsBuilderMap = new HashMap<>();

		for (EntryFieldBuilder builder : SpringApplicationContext.getAllBeansOfType(EntryFieldBuilder.class)) {
			if (!builder.getSupportedFields().isEmpty()) {
				for (EntryField indexedField : builder.getSupportedFields()) {
					NPreconditions.checkTrue(!(fieldsBuilderMap.containsKey(indexedField)), "The field " + indexedField.getName() + " cannot be indexed by several builders: " + indexedField.getClass() + ", " + fieldsBuilderMap.get(indexedField));
					fieldsBuilderMap.put(indexedField, builder);
				}
			}

		}
		return fieldsBuilderMap;
	}
}
