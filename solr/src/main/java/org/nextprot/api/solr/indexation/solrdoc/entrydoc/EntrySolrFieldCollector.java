package org.nextprot.api.solr.indexation.solrdoc.entrydoc;

import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.core.EntrySolrField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Collect associated index field data from a neXtProt domain entry
 */
public abstract class EntrySolrFieldCollector {

	private final Map<EntrySolrField, Object> fields = new HashMap<>();

	protected void addEntrySolrFieldValue(EntrySolrField field, Object value) {

		NPreconditions.checkTrue(getCollectedFields().contains(field), "The field " + field.name() + " is not supported in " + getClass().getName());

		if (!fields.containsKey(field) && field.getType().equals(List.class)) {
			fields.put(field, new ArrayList());
		}

		if (field.getType().equals(List.class)) {
			((List) fields.get(field)).add(value); // multiValued = true
		} else {
			this.fields.put(field, value);
		}

	}

	public final <T> T getFieldValue(EntrySolrField field, Class<T> requiredType) {

		if (fields.containsKey(field)) {
			return requiredType.cast(fields.get(field));
		}
		return null;
	}
	
	public final void clear() {
		fields.clear();
	}

	/** Collect associated index field data from entry */
	public abstract void collect(Entry entry, boolean isGold);
	public abstract Collection<EntrySolrField> getCollectedFields();
}
