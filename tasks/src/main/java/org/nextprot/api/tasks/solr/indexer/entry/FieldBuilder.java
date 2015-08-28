package org.nextprot.api.tasks.solr.indexer.entry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.index.EntryIndex.Fields;

public abstract class FieldBuilder {

	boolean initialized = false;
	private Map<Fields, Object> fields = new HashMap<>();

	abstract public Collection<Fields> getSupportedFields();

	public final void initializeBuilder(Entry entry) {
		init(entry);
		initialized = true;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void addField(Fields field, Object value) {
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

	protected abstract void init(Entry entry);

	public final <T> T getFieldValue(Fields field, Class<T> requiredType) {

		// If it has not been yet initialized
		NPreconditions.checkTrue(initialized, "The builder has not been yet initialized, invoke 'initializeBuilder' method");

		return requiredType.cast(fields.get(field));

	}

}
