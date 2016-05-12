package org.nextprot.api.tasks.solr.indexer.entry.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.commons.utils.Tree;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Family;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.utils.TerminologyUtils;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;

@EntryFieldBuilder
public class CVFieldBuilder extends FieldBuilder {

	@Override
	protected void init(Entry entry) {

		Set <String> cv_acs = new HashSet<String>();
		Set <String> cv_ancestors_acs = new HashSet<String>();
		Set <String> cv_synonyms = new HashSet<String>();
		Set <String> top_acs = new HashSet<>(Arrays.asList("CVAN_0001","CVAN_0002","CVAN_0011")); // top level ancestors (Annotation, feature, and ROI)
		
		// CV accessions
		List<Annotation> annots = entry.getAnnotations();
		int cvac_cnt = 0;
		for (Annotation currannot : annots) {
			String category = currannot.getCategory();
			if(!category.equals("tissue specificity")) {
				String cvac = currannot.getCvTermAccessionCode();
				if (cvac != null) {
					if(category.startsWith("go ")) {
						List<AnnotationEvidence> evlist = currannot.getEvidences();
						// We don't index negative annotations
						if(evlist.size() == 1 && evlist.get(0).isNegativeEvidence()) continue;
					}
					addField(Fields.CV_ACS, cvac);
					cvac_cnt++;
					cv_acs.add(cvac); // No duplicates: this is a Set, will be used for synonyms and ancestors
					addField(Fields.CV_NAMES,  currannot.getCvTermName());
				}
			}
		}
		
		// Families (why not part of Annotations ?)
		for (Family family : entry.getOverview().getFamilies()) { 
			addField(Fields.CV_ACS, family.getAccession());
			addField(Fields.CV_NAMES,  family.getName() + " family");
			cv_acs.add(family.getAccession());
		}
		
		// Final CV acs, ancestors and synonyms
		System.err.println("cumputing CV ancestors for " +  cv_acs.size() + " terms...");
		Tree<CvTerm> tree = null;
		//Set<String> ancestors2 = null;
		Set<String> ancestors2 = new TreeSet<String>();
		for (String cvac : cv_acs) {
			CvTerm term = this.terminologyservice.findTerminologyByAccession(cvac);
			String category = term.getOntology();
			//System.err.println("category: " + category);
			List<String> ancestors = TerminologyUtils.getAllAncestors(term.getAccession(), terminologyservice);
			List<Tree<CvTerm>> treeList = this.terminologyservice.findTerminologyTreeList(TerminologyCv.valueOf(category));
			if(treeList.isEmpty()) 	ancestors2.clear();
			ancestors2 = this.terminologyservice.getAncestorSets(treeList, term.getAccession());
			//Set<String> ancestors2 = TerminologyUtils.getAncestorSets(tree, term.getAccession());
			if(ancestors.size() != ancestors2.size()) {
				// Differences for FA-, KW-, SL-,  DO-, and enzymes...
				System.err.println(cvac + " old method: " + ancestors.size() + " new method: " + ancestors2.size() + " category" + category);
				//System.err.println(ancestors);
			}
			if(ancestors != null) 
				//cv_ancestors_acs.addAll(ancestors);
			  for (String ancestor : ancestors) {
                  cv_ancestors_acs.add(ancestor); 
			  }
			List<String> synonyms = term.getSynonyms();
			if(synonyms != null) { //if (term.getOntology().startsWith("Go")) System.err.println("adding: " + synonyms.get(0));
			  for (String synonym : synonyms)
                  cv_synonyms.add(synonym.trim()); // No duplicate: this is a Set
			      }
		}
		
		// Remove uninformative top level ancestors (Annotation, feature, and ROI)
		cv_ancestors_acs.removeAll(top_acs);
		// Index generated sets
		for (String ancestorac : cv_ancestors_acs) {
			addField(Fields.CV_ANCESTORS_ACS, ancestorac);
			addField(Fields.CV_ANCESTORS, this.terminologyservice.findTerminologyByAccession(ancestorac).getName());
		}
		System.err.println("CV ancestors done.");

		for (String synonym : cv_synonyms) {
			addField(Fields.CV_SYNONYMS, synonym);
		}
		
		
		List<CvTerm> enzymes = entry.getEnzymes();
		String ec_names = "";
		for (CvTerm currenzyme : enzymes) {
			cvac_cnt++;
			cv_acs.add(currenzyme.getAccession());
			addField(Fields.CV_NAMES, currenzyme.getName());
			if(ec_names != "") ec_names += ", ";
			ec_names += "EC " + currenzyme.getAccession();
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
	
}
