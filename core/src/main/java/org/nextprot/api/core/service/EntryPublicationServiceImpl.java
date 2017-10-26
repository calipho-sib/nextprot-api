package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.publication.EntryPublication;
import org.nextprot.api.core.domain.publication.EntryPublications;
import org.nextprot.api.core.domain.publication.PublicationProperty;
import org.nextprot.api.core.domain.publication.PublicationType;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.ui.page.PageView;
import org.nextprot.api.core.ui.page.PageViewFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EntryPublicationServiceImpl implements EntryPublicationService {

    @Autowired
    private EntryBuilderService entryBuilderService;

    @Cacheable("entry-publications")
    @Override
    public EntryPublications getEntryPublications(String entryAccession) {

        EntryPublicationsBuilder builder = new EntryPublicationsBuilder(entryBuilderService.build(EntryConfig.newConfig(entryAccession).withEverything()));

        return builder.build();
    }

    private static class EntryPublicationsBuilder {

        // values should be equal to cv_databases.cv_name
        private static final String PUBMED_DB="PubMed", NEXTPROT_SUBMISSION_DB="neXtProtSubmission";

        private final Entry entry;
        private final Map<String,Long> accession2id;

        EntryPublicationsBuilder(Entry entry) {

            this.entry = entry;
            accession2id = buildAccessionToIdMap(entry.getPublications());
        }

        public EntryPublications build() {

            Map<Long, EntryPublication> reportData = new HashMap<>();

            // handle publications cited in annotation evidences (link A)
            entry.getAnnotations()
                    .forEach(annot -> annot.getEvidences()
                            .forEach(evi -> handlePublicationAnnotationEvidence(annot, evi, reportData)));

            // handle direct links (B and C) but publications with A link are further processed also
            entry.getPublications()
                    .forEach(p -> handlePublicationDirectLinks(p, reportData));

            EntryPublications entryPublications = new EntryPublications();

            entryPublications.setEntryAccession(entry.getUniqueName());
            entryPublications.setReportData(reportData);

            return entryPublications;
        }

        private Map<String,Long> buildAccessionToIdMap(List<Publication> pubs) {
            Map<String,Long> map = new HashMap<>();
            for (Publication p: pubs) {
                String accession = getPubMedOrNextProtSubmissionAc(p);
                if (accession != null) map.put(accession, p.getPublicationId());
            }
            return map;
        }

        private String getPubMedOrNextProtSubmissionAc(Publication p) {
            if (! p.hasDbXrefs()) return null;
            for (DbXref x: p.getDbXrefs()) {
                if (PUBMED_DB.equals(x.getDatabaseName())) return x.getAccession();
                if (NEXTPROT_SUBMISSION_DB.equals(x.getDatabaseName())) return x.getAccession();
            }
            return null;
        }

        private void addEntryPublicationToReportDataIfNecessary(long pubId, Map<Long,EntryPublication> reportData) {
            if (! reportData.containsKey(pubId)) reportData.put(pubId, new EntryPublication(entry.getUniqueName(), pubId));
        }

        private void handlePublicationAnnotationEvidence(Annotation annot, AnnotationEvidence evi, Map<Long,EntryPublication> reportData) {

            // normal case
            Long pubId = null;
            if (evi.isResourceAPublication()) {
                pubId = evi.getResourceId();
                // special cases with indirect link to publication via an evidence xref
            } else if (PUBMED_DB.equals(evi.getResourceDb()) || NEXTPROT_SUBMISSION_DB.equals(evi.getResourceDb())) {
                String ac = evi.getResourceAccession();
                pubId = accession2id.get(ac);
            }
            if (pubId != null) {
                addEntryPublicationToReportDataIfNecessary(pubId, reportData);
                EntryPublication ep = reportData.get(pubId);
                ep.setCited(true);

                // add stuff for citedInViews
                for (PageView pv: PageViewFactory.getPageViews()) {
                    if (pv.doesDisplayAnnotationCategory(annot.getAPICategory())) {
                        String link = "/entry/" + entry.getUniqueName() + "/" + pv.getLink();
                        ep.addCitedInViews(pv.getLabel(), link);
                    }
                }
            }
        }

        private void handlePublicationDirectLinks(Publication p, Map<Long,EntryPublication> reportData) {
            long pubId = p.getPublicationId();
            addEntryPublicationToReportDataIfNecessary(pubId, reportData);
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

        private void handlePublicationFlagsByType(Publication p, EntryPublication ep) {

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
}
