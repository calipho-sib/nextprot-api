package org.nextprot.api.core.domain;

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

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
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
		if (fInfoCanonical.size()==0) {
			Set<Annotation> goFuncSet = new TreeSet<>((e1, e2) -> {

                int c; // GOLD over SILVER, then GO_BP over GO_MF, then Alphabetic in term name cf: jira NEXTPROT-1238
                c = e1.getQualityQualifier().compareTo(e2.getQualityQualifier());
                if (c == 0) c = e1.getCategory().compareTo(e2.getCategory());
                if (c == 0) c=e1.getCvTermName().compareTo(e2.getCvTermName());
                return c;
            });
			List<Annotation> annots = entry.getAnnotations();
			for (Annotation currannot : annots) {
				String category = currannot.getCategory();
				if(category.equals("go biological process") || category.equals("go molecular function")) {
				  goFuncSet.add(currannot); }
			}
			int rescnt = 0;
			for (Annotation resannot : goFuncSet) {
				// Stick term's name in the returned list
				if(resannot.getCvTermName().equals("protein binding") && goFuncSet.size() > 3) // avoid unsignificant function if possible
					continue;
				if(rescnt++ < 3) // return max 3 first annotation descriptions
					fInfoCanonical.add(resannot.getCvTermName());
				else break;
			}
		}

		return fInfoCanonical;
	 }
	
	public static boolean wouldUpgradeToPE1AccordingToOldRule(Entry e) {
		
		if (e.getProteinExistenceLevel()==1) return false; // already PE1
		if (e.getProteinExistenceLevel()==5) return false; // we don't upgrade PE5
		if (! e.getAnnotationsByCategory().containsKey("peptide-mapping")) return false; // no peptide mapping, no chance to upgrade to PE1		
		List<Annotation> list = e.getAnnotationsByCategory().get("peptide-mapping").stream()
				.filter(a -> AnnotationUtils.isProteotypicPeptideMapping(a)).collect(Collectors.toList());
		if (list==null) return false;
		if (AnnotationUtils.containsAtLeastNFeaturesWithSizeGreaterOrEqualsToS(list, 2, 7)) return true;
		if (AnnotationUtils.containsAtLeastNFeaturesWithSizeGreaterOrEqualsToS(list, 1, 9)) return true;
		return false;
	}
}
