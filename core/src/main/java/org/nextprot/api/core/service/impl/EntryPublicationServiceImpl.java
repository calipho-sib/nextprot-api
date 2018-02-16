package org.nextprot.api.core.service.impl;

import org.nextprot.api.core.dao.EntryPublicationDao;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Publication;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.publication.*;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.core.service.EntryPublicationService;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.api.core.ui.page.PageView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Logger;

@Service
public class EntryPublicationServiceImpl implements EntryPublicationService {

    private static Logger LOGGER = Logger.getLogger(EntryPublicationServiceImpl.class.getSimpleName());

    @Autowired
    private EntryPublicationDao entryPublicationDao;

    @Autowired
    private AnnotationService annotationService;

    @Autowired
    private PublicationService publicationService;

    @Cacheable("entry-publications")
    @Override
    public EntryPublications findEntryPublications(String entryAccession) {

        EntryPublications entryPublications = new EntryPublications();
        entryPublications.setEntryAccession(entryAccession);
        entryPublications.setData(new EntryPublicationMapBuilder(entryAccession).build());

        return entryPublications;
    }

    private class EntryPublicationMapBuilder {

        // values should be equal to cv_databases.cv_name
        private static final String PUBMED_DB = "PubMed";
        private static final String NEXTPROT_SUBMISSION_DB = "neXtProtSubmission";

        private final String entryAccession;
        private final Map<String, Long> accession2id;
        private final Map<Long, List<PublicationDirectLink>> directLinksByPubid;
        private final List<Publication> publications;
        private final List<Annotation> annotations;

        EntryPublicationMapBuilder(String entryAccession) {

            this.entryAccession = entryAccession;
            this.publications = publicationService.findPublicationsByEntryName(entryAccession);
            this.annotations = annotationService.findAnnotations(entryAccession);
            accession2id = buildAccessionToIdMap();
            directLinksByPubid = entryPublicationDao.findPublicationDirectLinks(entryAccession);
        }

        public Map<Long, EntryPublication> build() {

            Map<Long, EntryPublication> entryPublicationMap = new HashMap<>();

            // extract publications cited in annotation evidences (link A) and update references to PageViews
            annotations
                    .forEach(annotation -> annotation.getEvidences().stream()
                            .map(evidence -> extractPubIdFromEvidence(evidence))
                            .filter(Objects::nonNull)
                            .map(pubId -> entryPublicationMap.computeIfAbsent(pubId, k -> buildEntryPublication(pubId)))
                            .forEach(entryPublication -> {
                                entryPublication.setCited(true);
                                entryPublication.addCitedInViews(PageView.getDisplayablePageViews(annotation));
                            }));

            // handle direct links (B and C) but publications with A link are further processed also
            publications
                    .forEach(publication -> {
                        long pubId = publication.getPublicationId();
                        EntryPublication entryPublication = entryPublicationMap.computeIfAbsent(pubId, k -> buildEntryPublication(pubId));
                        handlePublicationDirectLinks(entryPublication);
                        handlePublicationFlagsByType(entryPublication, publication.getPublicationType());
                    });

            return entryPublicationMap;
        }

        private EntryPublication buildEntryPublication(long publicationId) {

            EntryPublication entryPublication = new EntryPublication(entryAccession, publicationId);
            entryPublication.setDirectLinks(directLinksByPubid.getOrDefault(publicationId, new ArrayList<>()));

            return entryPublication;
        }

        private Map<String,Long> buildAccessionToIdMap() {
            Map<String,Long> map = new HashMap<>();
            for (Publication p: publications) {
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

            Long l = null;

            if (evi.isResourceAPublication()) {
                l = evi.getResourceId();
                // special cases with indirect link to publication via an evidence xref
            } else if ("PubMed".equals(evi.getResourceDb()) || "neXtProtSubmission".equals(evi.getResourceDb())) {
                String ac = evi.getResourceAccession();
                l = accession2id.get(ac);
            }

            // TODO: should be removed while AnnotationBuilder continue to set id to -1 publications missing in neXtProt DB !!!
            if (l != null && l < 0) {
                LOGGER.severe(evi.getResourceType()+ " evidence of resource accession "+evi.getResourceAccession()
                        + " from annotation id " + evi.getAnnotationId() + " of entry " + entryAccession
                        + " has an incorrect resource id of "+l);
                return null;
            }

            return l;
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
                ep.setCited(true); // have been reverted as we miss a lot of cited publications (always direct link B provided by UniProt)
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
