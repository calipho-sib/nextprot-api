package org.nextprot.api.core.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.AnnotationUtils;
import org.nextprot.api.core.utils.ExperimentalContextUtil;
import org.nextprot.api.core.utils.PublicationUtils;
import org.nextprot.api.core.utils.XrefUtils;

public class EntryUtils implements Serializable{
	
	private static final long serialVersionUID = 3009334685615648172L;

	public static Entry filterEntryBySubPart(Entry entry, AnnotationApiModel annotationCategory) {
		
		long now = System.currentTimeMillis();
		
		List<Annotation> annotations;
		List<DbXref> xrefs;
		List<Publication> publications;
		List<ExperimentalContext> experimentalContexts;
		
		// Filter if necessary
		if (annotationCategory != null) {
			
			annotations = AnnotationUtils.filterAnnotationsByCategory(entry.getAnnotations(), annotationCategory);
			Set<Long> xrefIds = AnnotationUtils.getXrefIdsForAnnotations(annotations);
			xrefIds.addAll(AnnotationUtils.getXrefIdsForInteractionsInteractants(annotations));
			xrefs = XrefUtils.filterXrefsByIds(entry.getXrefs(), xrefIds);
			publications = PublicationUtils.filterPublicationsByIds(entry.getPublications(), AnnotationUtils.getPublicationIdsForAnnotations(annotations));

			experimentalContexts = ExperimentalContextUtil.filterExperimentalContextsByIds(entry.getExperimentalContexts(), AnnotationUtils.getExperimentalContextIdsForAnnotations(annotations));
			entry.setAnnotations(annotations);
			entry.setXrefs(xrefs);
			entry.setPublications(publications);
			entry.setExperimentalContexts(experimentalContexts);

		}
		
		System.out.println("Time to end for " + annotationCategory + (System.currentTimeMillis() - now ));
		return entry;
	}

	
}
