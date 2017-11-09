package org.nextprot.api.core.service.impl;

import org.nextprot.api.core.dao.EntryPublicationDao;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.publication.*;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.EntryPublicationService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.ui.page.PageView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EntryPublicationServiceImpl implements EntryPublicationService {

    @Autowired
    private EntryPublicationDao entryPublicationDao;

    @Autowired
    private EntryBuilderService entryBuilderService;

    @Cacheable("entry-publications")
    @Override
    public EntryPublications findEntryPublications(String entryAccession) {

        EntryPublications entryPublications = new EntryPublications();
        entryPublications.setEntryAccession(entryAccession);
        entryPublications.setData(new EntryPublicationMapBuilder(entryBuilderService
                .build(EntryConfig.newConfig(entryAccession).withEverything())).build());

        return entryPublications;
    }

    private class EntryPublicationMapBuilder {

        // values should be equal to cv_databases.cv_name
        private static final String PUBMED_DB = "PubMed";
        private static final String NEXTPROT_SUBMISSION_DB = "neXtProtSubmission";

        private final Entry entry;
        private final Map<String, Long> accession2id;
        private final Map<Long, List<PublicationDirectLink>> directLinksByPubid;

        EntryPublicationMapBuilder(Entry entry) {

            this.entry = entry;
            accession2id = buildAccessionToIdMap(entry.getPublications());
            directLinksByPubid = entryPublicationDao.findPublicationDirectLinks(entry.getUniqueName());
        }

        public Map<Long, EntryPublication> build() {

            Map<Long, EntryPublication> entryPublicationMap = new HashMap<>();

            // extract publications cited in annotation evidences (link A) and update references to PageViews
            entry.getAnnotations()
                    .forEach(annotation -> annotation.getEvidences().stream()
                            .map(evidence -> extractPubIdFromEvidence(evidence))
                            .filter(Objects::nonNull)
                            .map(pubId -> entryPublicationMap.computeIfAbsent(pubId, k -> buildEntryPublication(entry.getUniqueName(), pubId)))
                            .forEach(entryPublication -> {
                                entryPublication.setCited(true);
                                entryPublication.addCitedInViews(PageView.getDisplayablePageViews(annotation));
                            }));

            // handle direct links (B and C) but publications with A link are further processed also
            entry.getPublications()
                    .forEach(publication -> {
                        long pubId = publication.getPublicationId();
                        EntryPublication entryPublication = entryPublicationMap.computeIfAbsent(pubId, k -> buildEntryPublication(entry.getUniqueName(), pubId));
                        handlePublicationDirectLinks(entryPublication);
                        handlePublicationFlagsByType(entryPublication, publication.getPublicationType());
                    });

            return entryPublicationMap;
        }

        private EntryPublication buildEntryPublication(String entryAccession, long publicationId) {

            EntryPublication entryPublication = new EntryPublication(entryAccession, publicationId);
            entryPublication.setDirectLinks(directLinksByPubid.getOrDefault(publicationId, new ArrayList<>()));

            return entryPublication;
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

        private Long extractPubIdFromEvidence(AnnotationEvidence evi) {

            if (evi.isResourceAPublication()) {
                return evi.getResourceId();
                // special cases with indirect link to publication via an evidence xref
            } else if (PUBMED_DB.equals(evi.getResourceDb()) || NEXTPROT_SUBMISSION_DB.equals(evi.getResourceDb())) {
                String ac = evi.getResourceAccession();
                return accession2id.get(ac);
            }

            return null;
        }

        private void handlePublicationDirectLinks(EntryPublication ep) {

            if (!ep.getDirectLinks(PublicationProperty.SCOPE).isEmpty()) {
                ep.setCited(true);
            }
            if (!ep.getDirectLinks(PublicationProperty.COMMENT).isEmpty() && !ep.isCited()) {
                ep.setUncited(true);
            }
        }

        private void handlePublicationFlagsByType(EntryPublication ep, PublicationType publicationType) {

            // by order of frequency to minimize comparisons
            if (publicationType==PublicationType.ARTICLE) {
                if (ep.isCited()) ep.setCurated(true);
                if (ep.isUncited()) ep.setAdditional(true);

            } else if (publicationType==PublicationType.SUBMISSION) {
                ep.setSubmission(true);

            } else if (publicationType==PublicationType.ONLINE_PUBLICATION) {
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
                ep.setSubmission(true);

            } else if (publicationType==PublicationType.DOCUMENT) {
                // don't need to deal with them: not found in data
            }
        }
    }
}
