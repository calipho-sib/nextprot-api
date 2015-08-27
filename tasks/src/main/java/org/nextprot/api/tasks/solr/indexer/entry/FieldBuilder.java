package org.nextprot.api.tasks.solr.indexer.entry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.index.EntryIndex.Fields;

public abstract class FieldBuilder {

	boolean initialized = false;
	private Map<String, Object> fields = new HashMap<>();

	abstract public Collection<Fields> getSupportedFields();

	public final void initializeBuilder(Entry entry) {
		init(entry);
		initialized = true;
	}
	
	protected void putField(Fields field, Object value){
		this.fields.put(field.getName(), value);
	}
	
	protected abstract void init(Entry entry);
	
	public final <T> T getFieldValue(Fields field, Class<T> requiredType) {

		//If it has not been yet initialized
		NPreconditions.checkTrue(initialized, "The builder has not been yet initialized, invoke 'initializeBuilder' method");

		return requiredType.cast(fields.get(field.getName()));
		
	}

}
