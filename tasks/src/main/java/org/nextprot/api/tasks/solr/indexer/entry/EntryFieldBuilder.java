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

	boolean isGold = false;

    public boolean isGold() {
		return isGold;
	}

	public void setGold(boolean isGold) {
		this.isGold = isGold;
	}

	boolean initialized = false;
	private Map<EntryField, Object> fields = new HashMap<>();

	abstract public Collection<EntryField> getSupportedFields();

	public final void initializeBuilder(Entry entry) {
		if(!initialized){
			//System.err.println("initializing FieldBuilder: " + this.toString());
			init(entry);
			initialized = true;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void addField(EntryField field, Object value) {
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

	public final <T> T getFieldValue(EntryField field, Class<T> requiredType) {

		// If it has not been yet initialized
		NPreconditions.checkTrue(initialized, "The builder has not been yet initialized, invoke 'initializeBuilder' method");

		return requiredType.cast(fields.get(field));

	}
	
	public final void reset() {
		initialized = false;
		fields.clear();
	}

}
