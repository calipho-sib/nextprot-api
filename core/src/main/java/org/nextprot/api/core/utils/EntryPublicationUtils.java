package org.nextprot.api.core.utils;

import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.publication.EntryPublication;
import org.nextprot.api.core.domain.publication.EntryPublications;
import org.nextprot.api.core.domain.publication.PublicationProperty;
import org.nextprot.api.core.domain.publication.PublicationType;
import org.nextprot.api.core.ui.page.PageView;
import org.nextprot.api.core.ui.page.PageViewFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntryPublicationUtils {

	// values should be equal to cv_databases.cv_name
	private static final String PUBMED_DB="PubMed", NEXTPROT_SUBMISSION_DB="neXtProtSubmission";

	public static EntryPublications fetchEntryPublications(Entry entry) {

		List<PageView> pageViewList = getPageViews();
		Map<String,Long> pmid2id = getPmid2IdMap(entry.getPublications());
		
		Map<Long,EntryPublication> reportData = new HashMap<>();
		
		// handle publications cited in annotation evidences (link A)
		entry.getAnnotations()
				.forEach(annot -> annot.getEvidences()
						.forEach(evi -> handlePublicationAnnotationEvidence(entry.getUniqueName(), annot, evi, pmid2id, pageViewList, reportData)));
		
		// handle direct links (B and C) but publications with A link are further processed also  
		entry.getPublications()
				.forEach(p -> handlePublicationDirectLinks(entry.getUniqueName(), p, reportData));

		EntryPublications report = new EntryPublications();

		report.setEntryAccession(entry.getUniqueName());
		report.setReportData(reportData);

		return report;
	}

	private static String getPubMedOrNextProtSubmissionAc(Publication p) {
		if (! p.hasDbXrefs()) return null;
		for (DbXref x: p.getDbXrefs()) {
			if (PUBMED_DB.equals(x.getDatabaseName())) return x.getAccession();
			if (NEXTPROT_SUBMISSION_DB.equals(x.getDatabaseName())) return x.getAccession();
		}
		return null;
	}
		
	private static Map<String,Long> getPmid2IdMap(List<Publication> pubs) {
		Map<String,Long> map = new HashMap<>();
		for (Publication p: pubs) {
			String pmid = getPubMedOrNextProtSubmissionAc(p);
			if (pmid != null) map.put(pmid, p.getPublicationId());
		}
		return map;
	}
	
	private static void addEntryPublicationToReportDataIfNecessary(String entryAccession, long pubId, Map<Long,EntryPublication> reportData) {
		if (! reportData.containsKey(pubId)) reportData.put(pubId, new EntryPublication(entryAccession, pubId));
	}
	
	private static void handlePublicationAnnotationEvidence(String entryAc, Annotation annot, AnnotationEvidence evi, Map<String,Long> ac2idMap, List<PageView> pageViewList, Map<Long,EntryPublication> reportData) {
		// normal case
		Long pubId = null;
		if (evi.isResourceAPublication()) {
			pubId = evi.getResourceId();
		// special cases with indirect link to publication via an evidence xref
		} else if (PUBMED_DB.equals(evi.getResourceDb()) || NEXTPROT_SUBMISSION_DB.equals(evi.getResourceDb())) {
			String ac = evi.getResourceAccession();
			pubId = ac2idMap.get(ac);
		}
		if (pubId != null) {
			addEntryPublicationToReportDataIfNecessary(entryAc, pubId, reportData);
			EntryPublication ep = reportData.get(pubId);
			ep.setCited(true);
			
			// add stuff for citedInViews
			for (PageView pv: pageViewList) {
				if (pv.doesDisplayAnnotationCategory(annot.getAPICategory())) {
					String link = "/entry/" + entryAc + "/" + pv.getLink();
					ep.addCitedInViews(pv.getLabel(), link);
				}
			}
			
			
		};
	}

	private static List<PageView> getPageViews() {
		List<PageView> result = new ArrayList<>();
		for (PageViewFactory page : PageViewFactory.values()) {
			result.add(page.build());
		}
		return result;
	}
	
	private static void handlePublicationDirectLinks(String entryAc, Publication p, Map<Long,EntryPublication> reportData) {
		long pubId = p.getPublicationId();
		addEntryPublicationToReportDataIfNecessary(entryAc, pubId, reportData);
		EntryPublication ep = reportData.get(pubId);
        //List<String> scopes = p.getProperty("scopes");
		if (!p.getDirectLinks(PublicationProperty.SCOPE).isEmpty()) {
			ep.setCited(true);
		}
		//List<String> comments = p.getProperty("comment");
		if (!p.getDirectLinks(PublicationProperty.COMMENT).isEmpty() && !ep.isCited()) {
			ep.setUncited(true);
		}
		handlePublicationFlagsByType(p,ep);
	}
	
	private static void handlePublicationFlagsByType(Publication p, EntryPublication ep) {
		
		// by order of frequency to minimize comparisons
		PublicationType publicationType = PublicationType.valueOfName(p.getPublicationType());

		if (publicationType==PublicationType.ARTICLE) {
			if (ep.isCited()) ep.setCurated(true);
			if (ep.isUncited()) ep.setAdditional(true);
			
		} else if (publicationType==PublicationType.SUBMISSION) {
			ep.setSubmission(true);
			
		} else if (publicationType==PublicationType.ONLINE_PUBLICATION) {
			ep.setCited(true); // always direct link B provided by UniProt
			ep.setOnline(true);
			
		} else if (publicationType==PublicationType.BOOK) {
			if (ep.isCited()) ep.setCurated(true);
			if (ep.isUncited()) ep.setAdditional(true);
			
		} else if (publicationType==PublicationType.THESIS) {
			if (ep.isCited()) ep.setCurated(true);
			if (ep.isUncited()) ep.setAdditional(true);
			
		} else if (publicationType==PublicationType.PATENT) {
			ep.setPatent(true);
			
		} else if (publicationType==PublicationType.UNPUBLISHED_OBSERVATION) {
			ep.setCited(true);
			ep.setCurated(true);	
			
		} else if (publicationType==PublicationType.DOCUMENT) {
			// don't need to deal with them: not found in data
		} 
	}
}
