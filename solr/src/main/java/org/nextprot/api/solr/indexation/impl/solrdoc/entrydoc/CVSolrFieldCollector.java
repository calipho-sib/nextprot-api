package org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc;

import org.apache.log4j.Logger;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.ExperimentalContext;
import org.nextprot.api.core.domain.Family;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.core.service.ExperimentalContextService;
import org.nextprot.api.core.service.OverviewService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.service.annotation.AnnotationUtils;
import org.nextprot.api.core.utils.EntryUtils;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.nextprot.api.commons.constants.PropertyApiModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CVSolrFieldCollector extends EntrySolrFieldCollector {

	protected static final Logger LOGGER = Logger.getLogger(CVSolrFieldCollector.class);

	@Autowired
	private AnnotationService annotationService;

	@Autowired
	private ExperimentalContextService experimentalContextService;

	@Autowired
	private OverviewService overviewService;

	@Autowired
	private TerminologyService terminologyService;

	@Override
	public void collect(Map<EntrySolrField, Object> fields, String entryAccession, boolean gold) {

		boolean buildingSilverIndex = !gold;

		Set<String> cvTermsSetForAncestors = new HashSet<>();

		List<Annotation> annots = annotationService.findAnnotations(entryAccession);

		//Get cv terms related to normal annotations (except expressions)
		cvTermsSetForAncestors.addAll(setAndGetCvTermAnnotationsExceptExpression(fields, annots, buildingSilverIndex));

		//Get family names
		cvTermsSetForAncestors.addAll(setAndGetFamilyNames(fields, entryAccession));

		//Only cv terms from normal annotations and family are required to be indexed with their ancestors
		setAncestorsAndSynonyms(fields, entryAccession, cvTermsSetForAncestors);

		//Add more cv term accession
		setExperimentalContextAndPropertiesCvAccessionOnly(fields, annots, buildingSilverIndex);

		//Add enzyme names to EC_NAMES
		setEnzymeNames(fields, entryAccession);

	}


	@Override
	public Collection<EntrySolrField> getCollectedFields() {
		return Arrays.asList(EntrySolrField.CV_ANCESTORS_ACS, EntrySolrField.CV_ANCESTORS, EntrySolrField.CV_SYNONYMS, EntrySolrField.CV_NAMES, EntrySolrField.CV_ACS, EntrySolrField.EC_NAME);
	}


	private Set<String> setAndGetCvTermAnnotationsExceptExpression(Map<EntrySolrField, Object> fields, List<Annotation> annots, boolean buildingSilverIndex){

		Set<String> cv_acs = new HashSet<>();

		// CV accessions
		for (Annotation currannot : annots) {
			String category = currannot.getCategory();

			//Expression are set on another field, because we don't want to give them so much importance as normal annotations such as go
			//Example: An entry is almost always tested in all tissues, therefore if we would type in search for "liver" we would get as much as importance as proteins with function in liver
			if(!category.equals("tissue specificity")) { // tissue-specific CVs are indexed under 'expression'
				if(getCvTermFromAnnot(currannot).isPresent()){
					CvTerm term = getCvTermFromAnnot(currannot).get();

					//If there is no terms or tjey are all negative don't index
					if (AnnotationUtils.onlyNegativeEvidences(currannot))
						continue;

					//If we are building SILVER index always add, otherwise (we are building GOLD index) we need the annotation need to be GOLD.
					if(buildingSilverIndex || currannot.getQualityQualifier().equals("GOLD")) {
						addEntrySolrFieldValue(fields, EntrySolrField.CV_ACS, term.getAccession());
						addEntrySolrFieldValue(fields, EntrySolrField.CV_NAMES,  term.getName());
						cv_acs.add(term.getAccession()); // No duplicates: this is a Set, will be used for synonyms and ancestors
					}
				}

			}
		}

		return cv_acs;
	}

	private void setExperimentalContextAndPropertiesCvAccessionOnly(Map<EntrySolrField, Object> fields, List<Annotation> annots, boolean buildingSilverIndex) {

		Map<Long, List<CvTerm>> expCtxtCvTermMap = extractCvTermsFromExperimentalContext(annots);
		//We have added in CV_ACS the accessions related to experimental context and properties
		for (Annotation annot : annots) {
			List<CvTerm> terms = new ArrayList<>();
			if(buildingSilverIndex || annot.getQualityQualifier().equals("GOLD")) {
				//Check cv terms used in experimental context
				terms.addAll(extractCvTermsFromExperimentalContext(annot, expCtxtCvTermMap));
				terms.addAll(extractCvTermsFromEvidenceCodes(annot));
				terms.addAll(extractCvTermsFromProperties(annot));
				terms.addAll(extractCvTermsFromEvidenceProperties(annot));
			}
			for (CvTerm t : terms) {
				//Only add accessions in here. The use case is related to the page /term/TERM-NAME and see entries related to the term.
				//No need to index term name in here
				addEntrySolrFieldValue(fields, EntrySolrField.CV_ACS, t.getAccession());
			}
		}
	}

	private Set<String> setAndGetFamilyNames(Map<EntrySolrField, Object> fields, String entryAccession){

		Set<String> cv_acs = new HashSet<>();

		// Families (why not part of Annotations ?)
		for (Family family : overviewService.findOverviewByEntry(entryAccession).getFamilies()) {
			addEntrySolrFieldValue(fields, EntrySolrField.CV_ACS, family.getAccession());
			addEntrySolrFieldValue(fields, EntrySolrField.CV_NAMES,  family.getName() + " family");
			cv_acs.add(family.getAccession());
		}

		return cv_acs;

	}

	private void setAncestorsAndSynonyms(Map<EntrySolrField, Object> fields, String entryAccession, Set<String> cv_acs){

		// top level ancestors (Annotation, feature, and ROI)
		final Set<String> TOP_ACS = new HashSet<>(Arrays.asList("CVAN_0001","CVAN_0002","CVAN_0011"));

		Set<String> cv_synonyms = new HashSet<>();
		Set<String> cv_ancestors_acs = new HashSet<>();

		// Final CV acs, ancestors and synonyms
		for (String cvac : cv_acs) {
			CvTerm term = terminologyService.findCvTermByAccession(cvac);
			if (null==term) {
				LOGGER.error(entryAccession + " - term with accession |" + cvac + "| not found with findCvTermByAccession()");
				continue;
			}
			List<String> ancestors = terminologyService.getAllAncestorsAccession(term.getAccession());
			if(ancestors != null) {
				cv_ancestors_acs.addAll(ancestors);
			}
			List<String> synonyms = term.getSynonyms();
			if(synonyms != null) {
				for (String synonym : synonyms) cv_synonyms.add(synonym.trim()); // No duplicate: this is a Set
			}
		}

		// Remove uninformative top level ancestors (Annotation, feature, and ROI)
		cv_ancestors_acs.removeAll(TOP_ACS);

		// Index generated sets
		for (String ancestorac : cv_ancestors_acs) {
			addEntrySolrFieldValue(fields, EntrySolrField.CV_ANCESTORS_ACS, ancestorac);
			addEntrySolrFieldValue(fields, EntrySolrField.CV_ANCESTORS, terminologyService.findCvTermByAccessionOrThrowRuntimeException(ancestorac).getName());
		}

		for (String synonym : cv_synonyms) {
			addEntrySolrFieldValue(fields, EntrySolrField.CV_SYNONYMS, synonym);
		}

	}

	private void setEnzymeNames(Map<EntrySolrField, Object> fields, String entryAccession){

		List<CvTerm> enzymes = terminologyService.findEnzymeByMaster(entryAccession);
		String ec_names = "";
		for (CvTerm currenzyme : enzymes) {
			addEntrySolrFieldValue(fields, EntrySolrField.CV_NAMES, currenzyme.getName());
			if(ec_names != "") ec_names += ", ";
			ec_names += "EC " + currenzyme.getAccession();
			List <String> synonyms = currenzyme.getSynonyms();
			if(synonyms != null)
				for (String synonym : synonyms) {
					addEntrySolrFieldValue(fields, EntrySolrField.CV_SYNONYMS, synonym.trim());
				}
		}

		addEntrySolrFieldValue(fields, EntrySolrField.EC_NAME, ec_names);

	}

	// PRIVATE METHODS
	private Map<Long, List<CvTerm>> extractCvTermsFromExperimentalContext(List<Annotation> annots) {
		Map<Long, List<CvTerm>> expCtxtCvTermMap = new HashMap<>();
		List<ExperimentalContext> experimentalContexts = experimentalContextService.findExperimentalContextsByIds(EntryUtils.getExperimentalContextIds(annots));
		for (ExperimentalContext expCtxt : experimentalContexts) {

			List<CvTerm> contextTerms = new ArrayList<>();
			if(expCtxt.getDisease() != null) contextTerms.add(expCtxt.getDisease());
			if(expCtxt.getTissue() != null) contextTerms.add(expCtxt.getTissue());
			if(expCtxt.getDevelopmentalStage() != null) contextTerms.add(expCtxt.getDevelopmentalStage());
			if(expCtxt.getCellLine() != null) contextTerms.add(expCtxt.getCellLine());
			if(expCtxt.getOrganelle() != null) contextTerms.add(expCtxt.getOrganelle());
			// We don't index DetectionMethod because we index evidenceCode of the evidence
//			if(expCtxt.getDetectionMethod() != null) contextTerms.add(expCtxt.getDetectionMethod());
			if(!contextTerms.isEmpty()){
				expCtxtCvTermMap.put(expCtxt.getContextId(), contextTerms);
			}
		}

		return expCtxtCvTermMap;
	}

	static private Optional<CvTerm> getCvTermFromAnnot(Annotation annot) {

		if(annot.getCvTermAccessionCode() != null){
			CvTerm term = new CvTerm();
			term.setAccession(annot.getCvTermAccessionCode());
			term.setName(annot.getCvTermName());
			return Optional.of(term);
		}

		return Optional.empty();


	}

	static private List<CvTerm> extractCvTermsFromExperimentalContext(Annotation annot, Map<Long, List<CvTerm>> expCtxtCvTermMap) {

		List<CvTerm> terms = new ArrayList<>();

		//Don't get negative evidences
		List<Long> ctxtIds = annot.getEvidences().stream()
				.filter(e -> !e.isNegativeEvidence())
				.map(e -> e.getExperimentalContextId())
				.collect(Collectors.toList());

		for (Long ctxtId : ctxtIds) {
			List<CvTerm> ts = expCtxtCvTermMap.get(ctxtId);
			if(ts != null){
				terms.addAll(ts);
			}
		}
		return terms;
	}

	static private List<CvTerm> extractCvTermsFromEvidenceCodes(Annotation annot) {
		return annot.getEvidences().stream()
					.map(ev -> {
						CvTerm cv = new CvTerm();
						cv.setAccession(ev.getEvidenceCodeAC());
						cv.setName(ev.getEvidenceCodeName());
						return cv;
					})
					.collect(Collectors.toList());
	}

	static private List<CvTerm> extractCvTermsFromEvidenceProperties(Annotation annot) {
		List<CvTerm> result = new ArrayList<>();
		for (AnnotationEvidence ev: annot.getEvidences()) {
			if (ev.isNegativeEvidence()) continue;
			String ac = ev.getPropertyValue(PropertyApiModel.NAME_PSIMI_AC);
			String name = ev.getPropertyValue(PropertyApiModel.NAME_PSIMI_CV_NAME);
			if (ac!=null) {
				CvTerm term = new CvTerm();
				term.setAccession(ac);
				term.setName(name==null ? "" : name);
				result.add(term);				
			}
		}
		return result;
	}

	static private List<CvTerm> extractCvTermsFromProperties(Annotation annot) {

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
}
