package org.nextprot.api.tasks.solr.indexer.entry.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;

@EntryFieldBuilder
public class CVFieldBuilder extends FieldBuilder {

	Set <String> cv_acs = new HashSet<String>();
	Set <String> cv_ancestors_acs = new HashSet<String>();
	Set <String> cv_synonyms = new HashSet<String>();
	Set <String> cv_tissues = new HashSet<String>();
	
	@Override
	protected void init(Entry entry) {

		/*
	
		List<Annotation> annots = entry.getAnnotations();
		int cvac_cnt = 0;
		for (Annotation currannot : annots) {
			String category = currannot.getCategory();
			if (category.equals("tissue specificity")) {
				// No duplicates this is a Set
				cv_tissues.add(currannot.getCvTermAccessionCode()); 
				cv_tissues.add(currannot.getCvTermName()); 
			} else {
				String cvac = currannot.getCvTermAccessionCode();
				if (cvac != null) {
					doc.addField("cv_acs", cvac);
					cvac_cnt++;
					cv_acs.add(cvac); // No duplicates: this is a Set, will be
										// used for synonyms and ancestors
					doc.addField("cv_names", currannot.getCvTermName());
				}
			}
		}
		
		
				// Final CV acs, ancestors and synonyms
		for (String cvac : cv_acs) {
			Terminology term = this.terminologyservice.findTerminologyByAccession(cvac);
			String category = term.getOntology();
			//System.out.println(cvac + ": " + category);
			//if(term == null) System.err.println("problem with " + cvac);
			//else { System.err.println(cvac);
			List<String> ancestors = term.getAncestorAccession();
			if(ancestors != null) 
			  for (String ancestor : ancestors)
                  cv_ancestors_acs.add(ancestor); // No duplicate: this is a Set
			List<String> synonyms = term.getSynonyms();
			if(synonyms != null) { //if (term.getOntology().startsWith("Go")) System.err.println("adding: " + synonyms.get(0));
			  for (String synonym : synonyms)
                  cv_synonyms.add(synonym.trim()); // No duplicate: this is a Set
			      }
		}
		// Index generated sets
		for (String ancestorac : cv_ancestors_acs) {
			doc.addField("cv_ancestors_acs", ancestorac);
			doc.addField("cv_ancestors", this.terminologyservice.findTerminologyByAccession(ancestorac).getName());
		}

		for (String synonym : cv_synonyms) {
			doc.addField("cv_synonyms", synonym);
		}*/

	}

	@Override
	public Collection<Fields> getSupportedFields() {
		return null;
	}

}
