package org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc;

import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Collect associated index field data from a neXtProt domain entry
 */
public abstract class EntrySolrFieldCollector {

	protected void addEntrySolrFieldValue(Map<EntrySolrField, Object> fields, EntrySolrField field, Object value) {

		NPreconditions.checkTrue(getCollectedFields().contains(field), "The field " + field.name() + " is not supported in " + getClass().getName());

		if (!fields.containsKey(field) && field.getType().equals(List.class)) {
			fields.put(field, new ArrayList());
		}

		if (field.getType().equals(List.class)) {

			//noinspection unchecked
			List<Object> list = (List<Object>) fields.get(field);

			if (!list.contains(value)) {
				list.add(value); // multiValued = true
			}
		} else {
			fields.put(field, value);
		}
	}

	/** Collect associated index field data from entry */
	public abstract void collect(Map<EntrySolrField, Object> fields, String entryAccession, boolean isGold);
	public abstract Collection<EntrySolrField> getCollectedFields();
}
