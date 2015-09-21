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
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.utils.TerminologyUtils;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;

@EntryFieldBuilder
public class ExpressionFieldBuilder extends FieldBuilder {

	private TerminologyService terminologyservice;


	@Override
	protected void init(Entry entry) {

		//Extract the tissues where there is expression ....
		Set <String> cv_tissues = new HashSet<String>();
		for (Annotation currannot : entry.getAnnotations()) {
			if (currannot.getCategory().equals("tissue specificity")) {
				// Check there is a detected expression
				boolean allnegative = true;
				for(AnnotationEvidence ev : currannot.getEvidences()) if(!ev.isNegativeEvidence()) {allnegative = false; break;}
				if(!allnegative) {
				// No duplicates this is a Set
				cv_tissues.add(currannot.getCvTermAccessionCode());
				cv_tissues.add(currannot.getCvTermName());
				} //else System.err.println("No expression: " + currannot.getCvTermAccessionCode());
			}
		}

		// Expression
		SortedSet <String> cv_tissues_final = new TreeSet<String>();
		for (String cv : cv_tissues) {
			//cv_tissues_final.add(cv); // No duplicate: this is a Set
			if(cv.startsWith("TS-")) {
				Terminology term = terminologyservice.findTerminologyByAccession(cv);
				//if(cv_tissues_final.contains(cv)) System.err.println(cv + " already seen");
				//else System.err.println(cv);
				cv_tissues_final.add(cv); // No duplicate: this is a Set
				//List<String> ancestors = term.getAncestorAccession();
				List<String> ancestors = TerminologyUtils.getAllAncestors(term.getAccession(), terminologyservice);
				if(ancestors != null) 
				  for (String ancestorac : ancestors) {
					  //if(cv.equals("TS-0079")) System.err.println("blood ancestor: " + ancestorac);
					  cv_tissues_final.add(ancestorac);  // No duplicate: this is a Set
					  cv_tissues_final.add(terminologyservice.findTerminologyByAccession(ancestorac).getName());  // No duplicate: this is a Set
				  }
				List<String> synonyms = term.getSynonyms();
				if(synonyms != null) for (String synonym : synonyms)  cv_tissues_final.add(synonym); 
			}
			else cv_tissues_final.add(cv); // No duplicate: this is a Set
		}
		for (String cv : cv_tissues_final) {
			addField(Fields.EXPRESSION, cv.trim());
		}

	}


	@Override
	public Collection<Fields> getSupportedFields() {
		return Arrays.asList(Fields.EXPRESSION);
	}
	
	public void setTerminologyservice(TerminologyService terminologyservice) {
		this.terminologyservice = terminologyservice;
	}


}
