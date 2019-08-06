package org.nextprot.api.core.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.*;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.service.DbXrefService;
import org.nextprot.api.core.service.annotation.AnnotationUtils;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.service.impl.AnnotationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;


public class EntryUtils implements Serializable{	
	private static final long serialVersionUID = 3009334685615648172L;
	private static final Log LOGGER = LogFactory.getLog(EntryUtils.class);


	public static String getEntryName(String nextprotAccession) {
		String entryAccession = nextprotAccession;
		if((nextprotAccession != null) && (nextprotAccession.length() > 0) && (nextprotAccession.contains("-"))){
			entryAccession = nextprotAccession.substring(0, nextprotAccession.indexOf("-"));
		}
		return entryAccession;
	}

	public static Set<Long> getMdataIds(List<Annotation> annotations) {
		Set<Long> mdataIds = new TreeSet<>();
		if (annotations != null) {
			for (Annotation annot : annotations) {
				if (annot.getEvidences() != null) {
					for (AnnotationEvidence evi: annot.getEvidences()) {
						Long mdId = evi.getMdataId();
						if (mdId != null && mdId != 0) mdataIds.add(mdId);
					}
				}
			}
		}
		return mdataIds;
	}
	
	public static Set<Long> getExperimentalContextIds(List<Annotation> annotations) {
		Set<Long> ecIds = new TreeSet<>();
		if (annotations != null) {
			for (Annotation annot : annotations) {
				if (annot.getEvidences() != null) {
					for (AnnotationEvidence evi: annot.getEvidences()) {
						Long ecId = evi.getExperimentalContextId();
						if (ecId != null && ecId != 0) ecIds.add(ecId);
					}
				}
			}
		}
		return ecIds;
	}
	
	public static Entry filterEntryBySubPart(Entry entry, EntryConfig config) {
		
		List<Annotation> annotations;
		List<DbXref> xrefs;
		List<Publication> publications;
		List<ExperimentalContext> experimentalContexts;
		
		// Filter if necessary (config is applied and there are some annotations)
  		if ((config.hasSubPart() || config.hasGoldOnly()) && ((entry.getAnnotations() != null)) && (!entry.getAnnotations().isEmpty())) {

			annotations = AnnotationUtils.filterAnnotationsByCategory(entry, config.getSubpart(), config.hasGoldOnly());

			Set<String> dependencyHashes = new HashSet<String>();
			
			annotations.stream().filter(a -> a.isProteoformAnnotation()).forEach(a -> {
				for(String subject : a.getSubjectComponents()){
					dependencyHashes.add(subject);
				}
				dependencyHashes.add((a.getBioObject()).getAnnotationHash());
			});
			
			List<Annotation> dependentAnnotations = AnnotationUtils.filterAnnotationsByHashes(entry, dependencyHashes);

			if(config.hasGoldOnly()){
				Map<AnnotationCategory, List<Annotation>> dependentAnnotationsGroupedByCategory = dependentAnnotations.stream().collect(Collectors.groupingBy(Annotation::getAPICategory));
				dependentAnnotationsGroupedByCategory.entrySet().forEach(entrySet -> {
					annotations.addAll(AnnotationUtils.filterAnnotationsByCategory(entrySet.getValue(), entrySet.getKey(), true, config.hasGoldOnly()));
				});
			}else {
				annotations.addAll(dependentAnnotations);
			}
			
			entry.setAnnotations(annotations);
			
			if(!config.hasNoAdditionalReferences()){ //In case we don't care about xrefs, publications and experimental contexts (will be faster)

				// keep only xrefs, publications, exp. contexts related to loaded annotations 
				xrefs = XrefUtils.filterXrefsByIds(entry.getXrefs(), AnnotationUtils.getXrefIdsForAnnotations(annotations));
				publications = PublicationUtils.filterPublicationsByIds(entry.getPublications(), AnnotationUtils.getPublicationIdsForAnnotations(annotations));
				experimentalContexts = ExperimentalContextUtil.filterExperimentalContextsByIds(entry.getExperimentalContexts(), AnnotationUtils.getExperimentalContextIdsForAnnotations(annotations));
				entry.setXrefs(xrefs);
				entry.setPublications(publications);
				entry.setExperimentalContexts(experimentalContexts);
			}
		}
		
		return entry;
	}


	
	/**
	 * Builds a dictionary (HashMap) where the key is the annotation uniqueName and the value the annotation itself.
	 * @param entry
	 * @return a dictionary of annotations where the key is the annotation uniqueName (= identifier in both NP1 and BED world)
	 */
	public static Map<String,Annotation> getUniqueNameAnnotationMap(Entry entry) {
		
		Map<String,Annotation> result = new HashMap<String,Annotation>();
		for (Annotation annot: entry.getAnnotations()) {
			result.put(annot.getUniqueName(), annot);
		}
		return result;
	}
	
	public static Map<String,Integer> getAnnotationCategoryCountMap(Entry entry) {
		Map<String,Integer> result = new TreeMap<String,Integer>();
		for (Annotation annot: entry.getAnnotations()) {
			String key = annot.getApiTypeName();
			if (!result.containsKey(key)) result.put(key, new Integer(0));
			int value = result.get(key).intValue()+1;
			result.put(key, new Integer(value));
		}
		return result;
	}
	
	public static Map<String,Annotation> getHashAnnotationMap(Entry entry) {
		return getHashAnnotationMap(entry.getAnnotations());
	}

	/**
	 * Builds a dictionary (HashMap) where the key is the annotation annotationHash and the value the annotation itself.
	 * Annotations with no hash are skipped
	 * @param annotations
	 * @return a dictionary of annotations where the key is the annotation hash (= identifier in BED world)
	 */
	public static Map<String,Annotation> getHashAnnotationMap(List<Annotation> annotations) {
		
		Map<String,Annotation> result = new HashMap<String,Annotation>();
		for (Annotation annot: annotations) {
			if (annot.getAnnotationHash() != null && ! annot.getAnnotationHash().isEmpty()) {
				result.put(annot.getAnnotationHash(), annot);
			}
		}
		return result;
	}
		
	/**
	 * Returns a dictionary mapping proteoforms to their annotations.
	 * The key is the proteoform, the value the list of annotations related to it.
	 * Note that the method is "isoform "aware": only annotations having 
	 * an AnnotationIsoformSpecificity record in getTargetingIsoformsMap 
	 * for the isoformAc specified in the parameter are taken into account
	 * @param entry
	 * @param isoformAc
	 * @return
	 */
	public static Map<Proteoform,List<Annotation>> getProteoformAnnotationsMap(Entry entry, String isoformAc) {
		
		Map<Proteoform,List<Annotation>> result = new HashMap<Proteoform,List<Annotation>>();
		for (Annotation annot: entry.getAnnotations()) {
			if (annot.isProteoformAnnotation()) {
				if (annot.getTargetingIsoformsMap().containsKey(isoformAc)) {
					AnnotationIsoformSpecificity spec = annot.getTargetingIsoformsMap().get(isoformAc);
					Proteoform key = new Proteoform(isoformAc, spec.getName(), annot.getSubjectComponents());
					if (!result.containsKey(key)) result.put(key, new ArrayList<Annotation>());
					result.get(key).add(annot);
				}
			}
		}
		return result;
	}

	public static boolean wouldUpgradeToPE1AccordingToOldRule(Entry e) {

		ProteinExistence uniprotPE = e.getOverview().getProteinExistences().getProteinExistence(ProteinExistence.Source.PROTEIN_EXISTENCE_UNIPROT);

		if (uniprotPE == ProteinExistence.PROTEIN_LEVEL) return false; // already PE1
		if (uniprotPE == ProteinExistence.UNCERTAIN) return false; // we don't upgrade PE5
		if (! e.getAnnotationsByCategory().containsKey("peptide-mapping")) return false; // no peptide mapping, no chance to upgrade to PE1

		// See specs in: https://swissprot.isb-sib.ch/wiki/display/cal/neXtProt+Custom+HPP+files#neXtProtCustomHPPfiles-FileHPPentrieswithunconfirmedMSdata.txt
		// rule 2015-04: either 2 peptides 7 aa or 1 peptide 9 aa
		List<Annotation> list = e.getAnnotationsByCategory().get("peptide-mapping").stream()
				.filter(a -> AnnotationUtils.isProteotypicPeptideMapping(a)).collect(Collectors.toList());
		if (list==null) return false;
		if (AnnotationUtils.containsAtLeastNFeaturesWithSizeGreaterOrEqualsToS(list, 2, 7)) return true;
		if (AnnotationUtils.containsAtLeastNFeaturesWithSizeGreaterOrEqualsToS(list, 1, 9)) return true;
		return false;
	}

	/* 
	 * This method returns true for entries which are NOT promoted to PE1 according to the current rules 
	 * but which would have been promoted to PE1 according to earlier rules adopted by HUPO consortium 
	 * See also specs
	 * https://swissprot.isb-sib.ch/wiki/display/cal/neXtProt+Custom+HPP+files#neXtProtCustomHPPfiles-FileHPPentrieswithunconfirmedMSdata.txt
	 */
	public static boolean isUnconfirmedMS(Entry e) {
		// get PE according to current rules
		ProteinExistence np2PE = e.getOverview().getProteinExistences().getProteinExistence(ProteinExistence.Source.PROTEIN_EXISTENCE_NEXTPROT2);
		return np2PE != ProteinExistence.PROTEIN_LEVEL && wouldUpgradeToPE1AccordingToOldRule(e);
	}


	public static List<DbXref> getGnomADXrefs(List<Annotation> annotations, DbXrefService xrefService) {
		LOGGER.info("Annotations to process " + annotations.size());
		List<DbXref> gnomADXrefs = new ArrayList<>();
		annotations.stream()
				   .filter(annotation -> AnnotationCategory.VARIANT.getDbAnnotationTypeName().equals(annotation.getCategory()))
				   .forEach(annotation -> {
				   		// Generates dbxref for all evidence
					   annotation.getEvidences()
							   .stream()
							   .filter(annotationEvidence -> "gnomAD".equals(annotationEvidence.getResourceDb()))
							   .forEach(annotationEvidence -> {
							   		DbXref gnomadXref = new DbXref();
							   		try {
							   			long xrefId = xrefService.findXrefId("gnomAD", annotationEvidence.getResourceAccession());
							   			gnomadXref.setDbXrefId(xrefId);
							   			gnomadXref.setAccession(annotationEvidence.getResourceAccession());
							   			gnomadXref.setDatabaseCategory("Variant databases");
							   			gnomadXref.setDatabaseName("gnomAD");
							   			gnomadXref.setUrl("https://gnomad.broadinstitute.org");
							   			gnomadXref.setLinkUrl(CvDatabasePreferredLink.GNOMAD.getLink());
							   			gnomadXref.setProperties(new ArrayList<>());
							   			gnomADXrefs.add(gnomadXref);
							   		} catch(Exception e) {
							   			e.printStackTrace();
							   		}
							   });
				   });
		LOGGER.info("GNOMAD xrefs generated " + gnomADXrefs.size());
		return gnomADXrefs;
	}
	
}
