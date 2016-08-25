package org.nextprot.api.core.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.utils.ExperimentalContextUtil;
import org.nextprot.api.core.utils.PublicationUtils;
import org.nextprot.api.core.utils.XrefUtils;
import org.nextprot.api.core.utils.annot.AnnotationUtils;

public class EntryUtils implements Serializable{
	
	private static final long serialVersionUID = 3009334685615648172L;

	
	public static String getEntryName(String nextprotAccession) {
		String entryAccession = nextprotAccession;
		if((nextprotAccession != null) && (nextprotAccession.length() > 0) && (nextprotAccession.contains("-"))){
			entryAccession = nextprotAccession.substring(0, nextprotAccession.indexOf("-"));
		}
		return entryAccession;
	}

	public static Entry filterEntryBySubPart(Entry entry, EntryConfig config) {
		
		List<Annotation> annotations;
		List<DbXref> xrefs;
		List<Publication> publications;
		List<ExperimentalContext> experimentalContexts;
		
		// Filter if necessary
  		if (config.hasSubPart()) {

			annotations = AnnotationUtils.filterAnnotationsByCategory(entry, config.getSubpart());

			Set<String> dependencyHashes = new HashSet<String>();
			annotations.stream().filter(a -> a.isProteoformAnnotation()).forEach(a -> {
				for(String subject : a.getSubjectComponents()){
					dependencyHashes.add(subject);
				}
				dependencyHashes.add(((BioGenericObject)a.getBioObject()).getAnnotationHash());
			});
			
			List<Annotation> dependentAnnotations = AnnotationUtils.filterAnnotationsByHashes(entry, dependencyHashes);
			annotations.addAll(dependentAnnotations);
			
			entry.setAnnotations(annotations);
			
			if(!config.hasNoAdditionalReferences()){ //In case we don't care about xrefs, publications and experimental contexts (will be faster)

				Set<Long> xrefIds = AnnotationUtils.getXrefIdsForAnnotations(annotations);

				xrefIds.addAll(AnnotationUtils.getXrefIdsForInteractionsInteractants(annotations));
				xrefIds.addAll(AnnotationUtils.getXrefIdsFromAnnotations(annotations));
				xrefs = XrefUtils.filterXrefsByIds(entry.getXrefs(), xrefIds);
				publications = PublicationUtils.filterPublicationsByIds(entry.getPublications(), AnnotationUtils.getPublicationIdsForAnnotations(annotations));
				experimentalContexts = ExperimentalContextUtil.filterExperimentalContextsByIds(entry.getExperimentalContexts(), AnnotationUtils.getExperimentalContextIdsForAnnotations(annotations));
				entry.setXrefs(xrefs);
				entry.setPublications(publications);
				entry.setExperimentalContexts(experimentalContexts);
			}
		}
		
		return entry;
	}


	//TODO: PAM temporary method
	public static Set<Proteoform> getProteoformSet(Entry entry, String isoformAc) {
		
		Set<Proteoform> result = new HashSet<Proteoform>();
		for (Annotation annot: entry.getAnnotations()) {
			if (annot.isProteoformAnnotation()) {
				if (annot.getTargetingIsoformsMap().containsKey(isoformAc)) {
					AnnotationIsoformSpecificity spec = annot.getTargetingIsoformsMap().get(isoformAc);
					result.add(new Proteoform(isoformAc, spec.getName(), annot.getSubjectComponents()));
				}
			}
		}
		return result;
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
	
	private static void printMap(Map map) {
		for (Object k: map.keySet()) {
			System.out.println(k + " => " + map.get(k));
		}
	}
	
	/**
	 * Builds a dictionary (HashMap) where the key is the annotation annotationHash and the value the annotation itself.
	 * Annotations with no hash are skipped
	 * @param entry
	 * @return a dictionary of annotations where the key is the annotation hash (= identifier in BED world)
	 */
	public static Map<String,Annotation> getHashAnnotationMap(Entry entry) {
		
		//printMap(getAnnotationCategoryCountMap(entry));
		
		Map<String,Annotation> result = new HashMap<String,Annotation>();
		for (Annotation annot: entry.getAnnotations()) {
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
	

	public static List<String> getFunctionInfoWithCanonicalFirst(Entry entry) {
		List<String> fInfoCanonical = new  ArrayList<String>();
		List<String> fInfoNonCanonical = new  ArrayList<String>();
		List<Isoform> isos = entry.getIsoforms();
		String canonicalIso = "";
		
		// Get Id of the canonical (swissprotdisplayed) isoform
		for (Isoform curriso : isos)
			if(curriso.isCanonicalIsoform()) {
				canonicalIso = curriso.getUniqueName();
				break;
				}	
		
		// Get the function annotation and put it in the right basket
		for (Annotation currannot : entry.getAnnotations()) {
			if(currannot.getAPICategory().equals(AnnotationCategory.FUNCTION_INFO))
				if(currannot.isSpecificForIsoform(canonicalIso))
					fInfoCanonical.add(currannot.getDescription());
				else
					fInfoNonCanonical.add(currannot.getDescription());
		}
		
		// Merge the lists in a final unique list with canonical function first
		//System.err.println("before: " + fInfoCanonical);
		fInfoCanonical.addAll(fInfoNonCanonical);
		//System.err.println("after: " + fInfoCanonical);
		return fInfoCanonical;
	 }
}
