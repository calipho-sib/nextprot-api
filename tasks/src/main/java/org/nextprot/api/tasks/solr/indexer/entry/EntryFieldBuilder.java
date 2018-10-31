package org.nextprot.api.tasks.solr.indexer.entry;

import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.index.EntryField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class EntryFieldBuilder {

	private final Map<EntryField, Object> fields = new HashMap<>();

	abstract public Collection<EntryField> getSupportedFields();

	protected void addEntryFieldValue(EntryField field, Object value) {

		NPreconditions.checkTrue(getSupportedFields().contains(field), "The field " + field.name() + " is not supported in " + getClass().getName());

		if (!fields.containsKey(field) && field.getClazz().equals(List.class)) {
			fields.put(field, new ArrayList());
		}

		if (field.getClazz().equals(List.class)) {
			((List) fields.get(field)).add(value); // multiValued = true
		} else {
			this.fields.put(field, value);
		}

	}

	public abstract void collect(Entry entry, boolean isGold);

	public final <T> T getFieldValue(EntryField field, Class<T> requiredType) {

		if (fields.containsKey(field)) {
			return requiredType.cast(fields.get(field));
		}
		return null;
	}
	
	public final void reset() {
		fields.clear();
	}
}
