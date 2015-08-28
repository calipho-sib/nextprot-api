package org.nextprot.api.tasks.solr.indexer.entry.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Terminology;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;

@EntryFieldBuilder
public class ExpressionFieldBuilder extends FieldBuilder {

	private TerminologyService terminologyservice;


	@Override
	protected void init(Entry entry) {

		//Extract the tissues ....
		Set <String> cv_tissues = new HashSet<String>();
		for (Annotation currannot : entry.getAnnotations()) {
			if (currannot.getCategory().equals("tissue specificity")) {
				// No duplicates this is a Set
				cv_tissues.add(currannot.getCvTermAccessionCode()); 
				cv_tissues.add(currannot.getCvTermName()); 
			}
		}

		
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
		for (String cv : cv_tissues_final) {
			addField(Fields.EXPRESSION, cv.trim());
		}

	}


	@Override
	public Collection<Fields> getSupportedFields() {
		return Arrays.asList(Fields.EXPRESSION);
	}

}
