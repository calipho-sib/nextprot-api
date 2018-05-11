package org.nextprot.api.tasks.solr.indexer.entry.impl;

import org.apache.log4j.Logger;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.ExperimentalContext;
import org.nextprot.api.core.domain.Family;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;
import org.nextprot.api.core.service.annotation.AnnotationUtils;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;

import java.util.*;
import java.util.stream.Collectors;

@EntryFieldBuilder
public class CVFieldBuilder extends FieldBuilder {

	protected static final Logger LOGGER = Logger.getLogger(CVFieldBuilder.class);

	static Map<Long, List<CvTerm>> extractCvTermsFromExperimentalContext(Entry entry) {
		Map<Long, List<CvTerm>> expCtxtCvTermMap = new HashMap<>();

		for (ExperimentalContext expCtxt : entry.getExperimentalContexts()) {

			List<CvTerm> contextTerms = new ArrayList();
			if(expCtxt.getDisease() != null) contextTerms.add(expCtxt.getDisease());
			if(expCtxt.getTissue() != null) contextTerms.add(expCtxt.getTissue());
			if(expCtxt.getDevelopmentalStage() != null) contextTerms.add(expCtxt.getDevelopmentalStage());
			if(expCtxt.getCellLine() != null) contextTerms.add(expCtxt.getCellLine());
			if(expCtxt.getOrganelle() != null) contextTerms.add(expCtxt.getOrganelle());
			if(expCtxt.getDetectionMethod() != null) contextTerms.add(expCtxt.getDetectionMethod());
			if(!contextTerms.isEmpty()){
				expCtxtCvTermMap.put(expCtxt.getContextId(), contextTerms);
			}
		}

		return expCtxtCvTermMap;
	}

	static Optional<CvTerm> getCvTermFromAnnot(Annotation annot) {

		if(annot.getCvTermAccessionCode() != null){
			CvTerm term = new CvTerm();
			term.setAccession(annot.getCvTermAccessionCode());
			term.setName(annot.getCvTermName());
			return Optional.of(term);
		}

		return Optional.empty();


	}

	static List<CvTerm> extractCvTermsFromExperimentalContext(Annotation annot, Map<Long, List<CvTerm>> expCtxtCvTermMap) {

		List<CvTerm> terms = new ArrayList<>();

		//Don't get negative evidences
		List<Long> ctxtIds = annot.getEvidences().stream()
                             .filter(e -> !e.isNegativeEvidence())
                             .map(e -> e.getExperimentalContextId())
                             .collect(Collectors.toList());

		for (Long ctxtId : ctxtIds) {
			List<CvTerm> ts = expCtxtCvTermMap.get(ctxtId);
			if(ts != null){
				for (CvTerm t : ts) {
					terms.add(t);
				}
			}
		}
		return terms;
	}


	static List<CvTerm> extractCvTermsFromProperties(Annotation annot) {

		if(AnnotationUtils.onlyNegativeEvidences(annot)) {
			return new ArrayList<>();
		}

		List<CvTerm> terms = new ArrayList<>();
		for(AnnotationProperty ap : annot.getProperties()){
			if((ap != null) && (ap.getName() != null) && (ap.getName().equals("topology") || ap.getName().equals("orientation"))){
				CvTerm term = new CvTerm();
				term.setAccession(ap.getAccession());
				term.setName(ap.getValue());
				terms.add(term);
			}
		}
		return terms;
	}


	static void addToTermsToBeIndexed(List<CvTerm> termsToBeIndexed, Entry entry, String category, CvTerm ... terms) {

		for(CvTerm t : terms){
			if(t.getAccession().isEmpty()){
				LOGGER.warn("CVterm accession empty in " + category + " for " + entry.getUniqueName());
			}else {
				termsToBeIndexed.add(t);
			}
		}
	}


	@Override
	protected void init(Entry entry) {

		boolean BUILDING_SILVER_INDEX = !this.isGold();

		Set <String> cv_acs = new HashSet<String>();
		Set <String> cv_ancestors_acs = new HashSet<String>();
		Set <String> cv_synonyms = new HashSet<String>();
		Set <String> top_acs = new HashSet<>(Arrays.asList("CVAN_0001","CVAN_0002","CVAN_0011")); // top level ancestors (Annotation, feature, and ROI)
		
		// CV accessions
		List<Annotation> annots = entry.getAnnotations();
		Map<Long, List<CvTerm>> expCtxtCvTermMap = extractCvTermsFromExperimentalContext(entry);


		for (Annotation currannot : annots) {
			String category = currannot.getCategory();
			if(!category.equals("tissue specificity")) { // tissue-specific CVs are indexed under 'expression'
				List<CvTerm> termsToBeIndexed = new ArrayList<>();
				if(getCvTermFromAnnot(currannot).isPresent()){
					addToTermsToBeIndexed(termsToBeIndexed, entry, category, getCvTermFromAnnot(currannot).get());
				}


				//Add from experimental context
				addToTermsToBeIndexed(termsToBeIndexed, entry, category, extractCvTermsFromExperimentalContext(currannot, expCtxtCvTermMap).toArray(new CvTerm[0]));

				//Add from properties
				addToTermsToBeIndexed(termsToBeIndexed, entry, category, extractCvTermsFromProperties(currannot).toArray(new CvTerm[0]));

				//TODO CHECK for Expression (experimental context) Check for GOLD / SILVER and negative evidences a bit better...

				//If there is no terms or tjey are all negative don't index
				if (termsToBeIndexed.isEmpty() && AnnotationUtils.onlyNegativeEvidences(currannot))
					continue;

				//If we are building SILVER index always add, otherwise (we are building GOLD index) we need the annotation need to be GOLD.
				if(BUILDING_SILVER_INDEX || currannot.getQualityQualifier().equals("GOLD")) {
					for (CvTerm cvTerm : termsToBeIndexed) {
						addField(Fields.CV_ACS, cvTerm.getAccession());
						addField(Fields.CV_NAMES,  cvTerm.getName());
						cv_acs.add(cvTerm.getAccession()); // No duplicates: this is a Set, will be used for synonyms and ancestors
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
				LOGGER.error(entry.getUniqueName() + " - term with accession |" + cvac + "| not found with findCvTermByAccession()");
				continue;
			}
			List<String> ancestors = terminologyservice.getAllAncestorsAccession(term.getAccession());
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
