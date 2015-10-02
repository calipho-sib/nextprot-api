package org.nextprot.api.tasks.solr.indexer.entry.impl;

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

		List<Annotation> annots = entry.getAnnotations();
		for (Annotation currannot : annots) {

			String category = currannot.getCategory();
			if (category.equals("function")){
				addField(Fields.FUNCTION_DESC, currannot.getDescription());
			}
			// We also should exclude uninformative category 'sequence conflict'
			else if(!currannot.getCategory().equals("tissue specificity")) {//System.err.println(currannot.getCategory());
				String desc = currannot.getDescription();
				if (desc != null) {
					addField(Fields.ANNOTATIONS, currannot.getCategory() + ": " + desc);
				}
			}
		}
		
	}


	@Override
	public Collection<Fields> getSupportedFields() {
		return Arrays.asList(Fields.ANNOTATIONS, Fields.FUNCTION_DESC);
	}

}
