package org.nextprot.api.core.utils;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.publication.PublicationType;

import sun.net.www.content.audio.x_aiff;

import com.sun.xml.internal.ws.api.addressing.AddressingVersion.EPR;

public class EntryPublicationUtils {
	
	// values should be equal to cv_databases.cv_name
	private static final String PUBMED_DB="PubMed", NEXTPROT_SUBMISSION_DB="neXtProtSubmission";  

	public static EntryPublicationReport buildReport(Entry entry) {

		List<Long> orderedPubIdList = entry.getPublications().stream().map(p -> p.getPublicationId()).collect(Collectors.toList());
		
		Map<String,Long> pmid2id = getPmid2IdMap(entry.getPublications());
		
		Map<Long,EntryPublication> reportData = new HashMap<Long,EntryPublication>();
		
		// handle publications cited in annotation evidences (link A)
		entry.getAnnotations().stream()
			.forEach(annot -> annot.getEvidences().stream()
					.forEach(evi -> handlePublicationAnnotationEvidence(annot, evi, pmid2id ,reportData)));
		
		// handle direct links (B and C) but publications with A link are further processed also  
		entry.getPublications().stream().forEach(p -> handlePublicationDirectLinks(p, reportData));
		
		return new EntryPublicationReport(reportData, orderedPubIdList);
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
	
	private static void addEntryPublicationToReportDataIfNecessary(long pubId, Map<Long,EntryPublication> reportData) {
		if (! reportData.containsKey(pubId)) reportData.put(pubId, new EntryPublication(pubId));
	}
	
	private static void handlePublicationAnnotationEvidence(Annotation annot, AnnotationEvidence evi, Map<String,Long> ac2idMap, Map<Long,EntryPublication> reportData) {
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
			addEntryPublicationToReportDataIfNecessary(pubId, reportData);
			EntryPublication ep = reportData.get(pubId);
			ep.setCited(true);
			// TODO add stuff for citedInVews
		};
	}

	private static void handlePublicationDirectLinks(Publication p, Map<Long,EntryPublication> reportData) {
		long pubId = p.getPublicationId();
		addEntryPublicationToReportDataIfNecessary(pubId, reportData);
		EntryPublication ep = reportData.get(pubId);
		List<String> scopes = p.getProperty("scope");
		if (scopes.size()>0) {
			ep.setCited(true);
		}
		List<String> comments = p.getProperty("comment");
		if (comments.size()>0) {
			if (! ep.cited) ep.setUncited(true);
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
	
	
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 	
	
	public static class EntryPublicationReport {

		private Map<Long,EntryPublication> reportData;
		private List<Long> orderedPubIdList;
		
		public EntryPublicationReport(Map<Long,EntryPublication> reportData, List<Long> orderedPubIdList) {
			this.reportData = reportData;
			this.orderedPubIdList = orderedPubIdList;
		}
		
		public EntryPublication getEntryPublication(long pubId) {
			return reportData.get(pubId);
		}
		
		public List<EntryPublication> getEntryPublicationCitedList() {
			return orderedPubIdList.stream().map(id -> reportData.get(id)).filter(ep -> ep.isCited()).collect(Collectors.toList());
			//return reportData.entrySet().stream().filter(e -> e.getValue().isCited()).map(e -> e.getValue()).collect(Collectors.toList());
		}
	}
	
	public static class EntryPublication {
		
		private long id;
		private boolean cited, uncited, patent, submission, online, curated, additional;
		private List<String> citedInViews = new ArrayList<String>();
		
		public EntryPublication(long id) {
			this.id=id;
		}
		public long getId() {
			return id;
		}
		public boolean isCited() {
			return cited;
		}
		public void setCited(boolean cited) {
			this.cited = cited;
		}
		public boolean isCurated() {
			return curated;
		}
		public void setCurated(boolean curated) {
			this.curated = curated;
		}
		public boolean isUncited() {
			return uncited;
		}
		public void setUncited(boolean uncited) {
			this.uncited = uncited;
		}
		public boolean isAdditional() {
			return additional;
		}
		public void setAdditional(boolean additional) {
			this.additional = additional;
		}
		public boolean isPatent() {
			return patent;
		}
		public void setPatent(boolean patent) {
			this.patent = patent;
		}
		public boolean isSubmission() {
			return submission;
		}
		public void setSubmission(boolean submission) {
			this.submission = submission;
		}
		public boolean isOnline() {
			return online;
		}
		public void setOnline(boolean online) {
			this.online = online;
		}
		public List<String> getCitedInViews() {
			return citedInViews;
		}
		public void setCitedInViews(List<String> citedInViews) {
			this.citedInViews = citedInViews;
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("id:").append(id).append(" ");
			sb.append("cited:").append(cited).append(" ");
			sb.append("uncited:").append(uncited).append(" ");
			sb.append("patent:").append(patent).append(" ");
			sb.append("submission:").append(submission).append(" ");
			sb.append("online:").append(online).append(" ");
			sb.append("curated:").append(curated).append(" ");
			sb.append("additional:").append(additional).append(" ");
			sb.append("in views:");
			for (String v : citedInViews) sb.append(v).append(",");
			sb.append(" ");
			return sb.toString();
		}
	}

	
	
}
