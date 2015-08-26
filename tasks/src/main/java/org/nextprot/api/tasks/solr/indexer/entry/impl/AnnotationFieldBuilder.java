package org.nextprot.api.tasks.solr.indexer.entry.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;

@EntryFieldBuilder
public class AnnotationFieldBuilder implements FieldBuilder {

	private List<String> annotations = new ArrayList<>();
	private List<String> functionDesc = null;
	
	public AnnotationFieldBuilder(Entry entry) {
		init(entry);
	}

	private void init(Entry entry) {

		List<Annotation> annots = entry.getAnnotations();
		for (Annotation currannot : annots) {

			String category = currannot.getCategory();
			if (category.equals("function")){
				if(functionDesc == null) functionDesc = new ArrayList<String>();
				functionDesc.add(currannot.getDescription());
			}else {
				String desc = currannot.getDescription();
				if (desc != null) {
					annotations.add(desc);
				}
			}
		}

	}

	@Override
	public <T> T build(Entry entry, Fields field, Class<T> requiredType) {

		if (field.equals(Fields.ANNOTATIONS)) return requiredType.cast(annotations);
		if (field.equals(Fields.FUNCTION_DESC)) return requiredType.cast(functionDesc);

		throw new NextProtException("Unsupported type " + field);

	}

	@Override
	public Collection<Fields> getSupportedFields() {
		return Arrays.asList(Fields.ANNOTATIONS, Fields.FUNCTION_DESC);
	}

}
