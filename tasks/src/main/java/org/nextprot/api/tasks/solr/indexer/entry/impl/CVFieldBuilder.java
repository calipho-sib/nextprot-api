package org.nextprot.api.tasks.solr.indexer.entry.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.commons.utils.Tree;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Family;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.utils.TerminologyUtils;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.GenerateSolrIndex;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;

@EntryFieldBuilder
public class CVFieldBuilder extends FieldBuilder {

	protected Logger logger = Logger.getLogger(CVFieldBuilder.class);
	
	@Override
	protected void init(Entry entry) {

		Set <String> cv_acs = new HashSet<String>();
		Set <String> cv_ancestors_acs = new HashSet<String>();
		Set <String> cv_synonyms = new HashSet<String>();
		Set <String> top_acs = new HashSet<>(Arrays.asList("CVAN_0001","CVAN_0002","CVAN_0011")); // top level ancestors (Annotation, feature, and ROI)
		
		// CV accessions
		List<Annotation> annots = entry.getAnnotations();
		boolean allnegative;
		for (Annotation currannot : annots) {
			String category = currannot.getCategory();
			if(!category.equals("tissue specificity")) { // tissue-specific CVs are indexed under 'expression'
				String cvac = currannot.getCvTermAccessionCode();
				if (cvac == null) continue;
				if (cvac.isEmpty())
				   logger.warn("CVterm accession empty in " + category + " for " + entry.getUniqueName());
				else {
					if(category.startsWith("go ")) {
						allnegative = true;
						List<AnnotationEvidence> evlist = currannot.getEvidences();
						// We don't index negative annotations
						for(AnnotationEvidence ev : evlist)
							allnegative = allnegative & ev.isNegativeEvidence();
						if(allnegative == true)
							continue;
					}
					if(!this.isGold() || currannot.getQualityQualifier().equals("GOLD")) {
					addField(Fields.CV_ACS, cvac);
					cv_acs.add(cvac); // No duplicates: this is a Set, will be used for synonyms and ancestors
					addField(Fields.CV_NAMES,  currannot.getCvTermName());
					}
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
		for (String cvac : cv_acs) {
			CvTerm term = this.terminologyservice.findCvTermByAccession(cvac);
			if (null==term) {
				logger.error(entry.getUniqueName() + " - term with accession |" + cvac + "| not found with findCvTermByAccession()");
				continue;
			}
			List<String> ancestors = TerminologyUtils.getAllAncestorsAccession(term.getAccession(), terminologyservice);
			if(ancestors != null) {
			  for (String ancestor : ancestors) cv_ancestors_acs.add(ancestor); 
			}
			List<String> synonyms = term.getSynonyms();
			if(synonyms != null) { 
			  for (String synonym : synonyms) cv_synonyms.add(synonym.trim()); // No duplicate: this is a Set
			}
		}
		
		// Remove uninformative top level ancestors (Annotation, feature, and ROI)
		cv_ancestors_acs.removeAll(top_acs);

		// Index generated sets
		for (String ancestorac : cv_ancestors_acs) {
			addField(Fields.CV_ANCESTORS_ACS, ancestorac);
			addField(Fields.CV_ANCESTORS, this.terminologyservice.findCvTermByAccession(ancestorac).getName());
		}

		for (String synonym : cv_synonyms) {
			addField(Fields.CV_SYNONYMS, synonym);
		}
		
		
		List<CvTerm> enzymes = entry.getEnzymes();
		String ec_names = "";
		for (CvTerm currenzyme : enzymes) {
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
