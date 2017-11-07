package org.nextprot.api.core.service.impl;

import org.nextprot.api.commons.dao.MasterIdentifierDao;
import org.nextprot.api.core.domain.publication.EntryPublication;
import org.nextprot.api.core.domain.publication.GlobalPublicationStatistics;
import org.nextprot.api.core.service.EntryPublicationService;
import org.nextprot.api.core.service.PublicationStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PublicationStatisticsServiceImpl implements PublicationStatisticsService {

    @Autowired
    private MasterIdentifierDao masterIdentifierDao;

    @Autowired
    private EntryPublicationService entryPublicationService;

    @Cacheable("global-publication-statistics")
    @Override
    public GlobalPublicationStatistics getGlobalPublicationStatistics() {

        GlobalPublicationStatistics globalPublicationStatistics = new GlobalPublicationStatistics();

        Map<Long, List<EntryPublication>> entryPublicationsByPublicationId =
                buildEntryPublicationsMap();

        entryPublicationsByPublicationId.forEach((pubId, entryPublications) -> {

            GlobalPublicationStatistics.PublicationStatistics stats =
                    new PublicationStatisticsAnalyser(pubId, entryPublications).analyse();
            globalPublicationStatistics.putPublicationStatisticsById(pubId, stats);

            if (stats.isCited()) {
                globalPublicationStatistics.incrementNumberOfCitedPublications();
            }
            if (stats.isComputed()) {
                globalPublicationStatistics.incrementNumberOfComputationallyMappedPublications();
            }
            if (stats.isLargeScale()) {
                globalPublicationStatistics.incrementNumberOfLargeScalePublications();
            }
            if (stats.isCurated()) {
                globalPublicationStatistics.incrementNumberOfCuratedPublications();
            }

            globalPublicationStatistics.incrementTotalNumberOfPublications();
        });

        return globalPublicationStatistics;
    }

    // Memoized function that returns EntryPublications by publication id
    private Map<Long, List<EntryPublication>> buildEntryPublicationsMap() {

        Map<Long, List<EntryPublication>> entryPublicationsById = new HashMap<>();

        for (String entryAccession : masterIdentifierDao.findUniqueNames()) {

            Map<Long, EntryPublication> publicationsById = entryPublicationService.findEntryPublications(entryAccession).getEntryPublicationsById();

            for (Map.Entry<Long, EntryPublication> kv : publicationsById.entrySet()) {

                entryPublicationsById.computeIfAbsent(kv.getKey(), k -> new ArrayList<>())
                        .add(kv.getValue());
            }
        }

        return entryPublicationsById;
    }

    private static class PublicationStatisticsAnalyser {

        private final long publicationId;
        private final List<EntryPublication> entryPublications;

        private PublicationStatisticsAnalyser(long publicationId, List<EntryPublication> entryPublications) {

            this.publicationId = publicationId;
            this.entryPublications = entryPublications;
        }

        private GlobalPublicationStatistics.PublicationStatistics analyse() {

            GlobalPublicationStatistics.PublicationStatistics publicationStatistics = new GlobalPublicationStatistics.PublicationStatistics();
            publicationStatistics.setPublicationId(publicationId);

            publicationStatistics.setCited(isCited());
            publicationStatistics.setComputed(isComputationallyMappedPublication());
            publicationStatistics.setCurated(isManuallyCuratedPublication());
            publicationStatistics.setLargeScale(isLargeScalePublication());

            return publicationStatistics;
        }

        private boolean isCited() {
            return entryPublications.stream()
                    .anyMatch(ep -> ep.isCited());
        }

        /**
         * Rule: Any kind of publication which is never referred in an entry annotation evidence but directly mapped to the entry by and only by PIR
         */
        private boolean isComputationallyMappedPublication() {
            return entryPublications.stream()
                    .allMatch(ep -> ep.isUncited());
        }

        /**
         * Rule: A large scale publication (pf_largescale)
         * Any kind of publication which Is linked to 15 entries or more by directly or by annotation evidences
         */
        private boolean isLargeScalePublication() {
            return entryPublications.size() > 14;
        }

        /**
         * Rule: An article, book, thesis or unpublished observation* that is referred in 1 or more entry annotation
         * evidence(s) or directly mapped to the entry by a NON PIR source
         */
        private boolean isManuallyCuratedPublication() {
            return entryPublications.stream()
                    .anyMatch(ep -> ep.isCurated());
        }
    }
}
