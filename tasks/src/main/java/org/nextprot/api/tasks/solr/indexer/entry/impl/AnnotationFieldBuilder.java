package org.nextprot.api.tasks.solr.indexer.entry.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;

@EntryFieldBuilder
public class AnnotationFieldBuilder extends FieldBuilder {

	
	@Override
	protected void init(Entry entry) {

		List<String> annotations = new ArrayList<>();
		List<String> functionDesc = null;

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
		
		super.putField(Fields.ANNOTATIONS, annotations);
		super.putField(Fields.FUNCTION_DESC, functionDesc);

	}


	@Override
	public Collection<Fields> getSupportedFields() {
		return Arrays.asList(Fields.ANNOTATIONS, Fields.FUNCTION_DESC);
	}

}
