package org.nextprot.api.tasks.solr.indexer.entry.impl;

import org.apache.log4j.Logger;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.solr.index.EntryField;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

@Service
public class ExpressionFieldBuilder extends EntryFieldBuilder {

	protected Logger logger = Logger.getLogger(ExpressionFieldBuilder.class);

	@Autowired
	private TerminologyService terminologyService;

	@Override
	protected void init(Entry entry) {
		//Extract the tissues where there is expression ....
		Set <String> cv_tissues = new HashSet<String>();
		for (Annotation currannot : entry.getAnnotations()) {
			if (currannot.getCategory().equals("tissue specificity")) {
				// Check there is a detected expression
				boolean allnegative = true;
				for(AnnotationEvidence ev : currannot.getEvidences())
					if(!ev.isNegativeEvidence() && (!this.isGold() || ev.getQualityQualifier().equals("GOLD")))
						// Only a GOLD positive evidence can invalidate allnegative in the GOLD index
				      {allnegative = false; break;}
				if(!allnegative) {
				// No duplicates this is a Set
				if(!this.isGold() || currannot.getQualityQualifier().equals("GOLD")) {
					cv_tissues.add(currannot.getCvTermAccessionCode());
					cv_tissues.add(currannot.getCvTermName());
					}
				} //else System.err.println("No expression: " + currannot.getCvTermAccessionCode());
			}
		}

		// Expression (without stages and expression_levels)
		SortedSet <String> cv_tissues_final = new TreeSet<String>();
		for (String cv : cv_tissues) {
			cv_tissues_final.add(cv);
			if(cv.startsWith("TS-")) {
				CvTerm term = terminologyService.findCvTermByAccession(cv);
				if (null==term) {
					// there is nothing more we can add to indexed fields (ancestors, synonyms), so let's return
					logger.error(entry.getUniqueName() + " - term with accession |" + cv + "| not found with findCvTermByAccession()");
					continue;
				}
				List<String> ancestors = terminologyService.getAllAncestorsAccession(term.getAccession());
				if(ancestors != null) 
				  for (String ancestorac : ancestors) {
					  cv_tissues_final.add(ancestorac);  
					  cv_tissues_final.add(terminologyService.findCvTermByAccessionOrThrowRuntimeException(ancestorac).getName());
				  }
				List<String> synonyms = term.getSynonyms();
				if(synonyms != null) for (String synonym : synonyms)  cv_tissues_final.add(synonym); 
			}
		}
		for (String cv : cv_tissues_final) {
			addField(EntryField.EXPRESSION, cv.trim());
		}

	}


	@Override
	public Collection<EntryField> getSupportedFields() {
		return Arrays.asList(EntryField.EXPRESSION);
	}
	
}
