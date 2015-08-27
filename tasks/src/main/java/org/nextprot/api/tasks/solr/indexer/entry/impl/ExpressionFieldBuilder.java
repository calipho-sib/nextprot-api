package org.nextprot.api.tasks.solr.indexer.entry.impl;

import java.util.Collection;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;

@EntryFieldBuilder
public class ExpressionFieldBuilder extends FieldBuilder {

	@Override
	protected void init(Entry entry) {

		/*
		// Expression
		SortedSet <String> cv_tissues_final = new TreeSet<String>();
		for (String cv : cv_tissues) {
			cv_tissues_final.add(cv); // No duplicate: this is a Set
			if(cv.startsWith("TS-")) {
				Terminology term = terminologyservice.findTerminologyByAccession(cv);
				List<String> ancestors = term.getAncestorAccession();
				if(ancestors != null) 
				  for (String ancestorac : ancestors) {
					  cv_tissues_final.add(ancestorac);  // No duplicate: this is a Set
					  cv_tissues_final.add(terminologyservice.findTerminologyByAccession(ancestorac).getName());  // No duplicate: this is a Set
				  }
				List<String> synonyms = term.getSynonyms();
				if(synonyms != null) for (String synonym : synonyms)  cv_tissues_final.add(synonym); 
			}
		}
		for (String cv : cv_tissues_final) doc.addField("expression", cv.trim());

		*/
	}


	@Override
	public Collection<Fields> getSupportedFields() {
		return null;
	}

}
