package org.nextprot.api.tasks.solr.indexer.entry.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Terminology;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;

@EntryFieldBuilder
public class CVFieldBuilder extends FieldBuilder {

	private TerminologyService terminologyservice;

	@Override
	protected void init(Entry entry) {

		Set <String> cv_acs = new HashSet<String>();
		Set <String> cv_ancestors_acs = new HashSet<String>();
		Set <String> cv_synonyms = new HashSet<String>();
		Set <String> cv_tissues = new HashSet<String>();

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
					addField(Fields.CV_ACS, cvac);
					cvac_cnt++;
					cv_acs.add(cvac); // No duplicates: this is a Set, will be used for synonyms and ancestors
					addField(Fields.CV_NAMES,  currannot.getCvTermName());
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
			addField(Fields.CV_ANCESTORS_ACS, ancestorac);
			addField(Fields.CV_ANCESTORS, this.terminologyservice.findTerminologyByAccession(ancestorac).getName());
		}

		for (String synonym : cv_synonyms) {
			addField(Fields.CV_SYNONYMS, synonym);
		}
		
		
		List<Terminology> enzymes = entry.getEnzymes();
		String ec_names = "";
		for (Terminology currenzyme : enzymes) {
			cvac_cnt++;
			cv_acs.add(currenzyme.getAccession());
			addField(Fields.CV_NAMES, currenzyme.getName());
			ec_names += "EC " + currenzyme.getAccession() + ", ";
			
			List <String> synonyms = currenzyme.getSynonyms();
			if(synonyms != null)
			   for (String synonym : synonyms) {
				   addField(Fields.CV_SYNONYMS, synonym.trim());
			   }
		}
		addField(Fields.EC_NAME, ec_names);
	}


	@Override
	public Collection<Fields> getSupportedFields() {
		return Arrays.asList(Fields.CV_ANCESTORS_ACS, Fields.CV_ANCESTORS, Fields.CV_SYNONYMS, Fields.CV_NAMES, Fields.CV_ACS, Fields.EC_NAME);
	}
	

	public void setTerminologyservice(TerminologyService terminologyservice) {
		this.terminologyservice = terminologyservice;
	}


}
