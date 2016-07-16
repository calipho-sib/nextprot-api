package org.nextprot.api.core.domain;

import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.utils.AnnotationUtils;
import org.nextprot.api.core.utils.ExperimentalContextUtil;
import org.nextprot.api.core.utils.PublicationUtils;
import org.nextprot.api.core.utils.XrefUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

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

	
}
