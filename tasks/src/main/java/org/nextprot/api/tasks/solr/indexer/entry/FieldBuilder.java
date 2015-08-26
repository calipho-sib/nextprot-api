package org.nextprot.api.tasks.solr.indexer.entry;

import java.util.Collection;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.index.EntryIndex.Fields;

public interface FieldBuilder {
	
	<T> T build(Entry entry, Fields field, Class<T> requiredType);
	Collection<Fields> getSupportedFields();

}
