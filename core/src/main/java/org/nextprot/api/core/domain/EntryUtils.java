package org.nextprot.api.core.domain;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.utils.AnnotationUtils;
import org.nextprot.api.core.utils.ExperimentalContextUtil;
import org.nextprot.api.core.utils.PublicationUtils;
import org.nextprot.api.core.utils.XrefUtils;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class EntryUtils implements Serializable{
	
	private static final long serialVersionUID = 3009334685615648172L;

	public static Entry filterEntryBySubPart(Entry entry, EntryConfig config) {
		
		List<Annotation> annotations;
		List<DbXref> xrefs;
		List<Publication> publications;
		List<ExperimentalContext> experimentalContexts;
		
		// Filter if necessary
  		if (config.hasSubPart()) {

			annotations = AnnotationUtils.filterAnnotationsByCategory(entry, config.getSubpart());
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
