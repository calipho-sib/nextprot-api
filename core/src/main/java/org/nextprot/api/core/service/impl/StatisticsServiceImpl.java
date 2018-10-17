package org.nextprot.api.core.service.impl;

import org.nextprot.api.core.domain.publication.EntryPublication;
import org.nextprot.api.core.domain.publication.GlobalPublicationStatistics;
import org.nextprot.api.core.service.GlobalPublicationService;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.api.core.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private GlobalPublicationService globalPublicationService;

    @Cacheable("global-publication-statistics")
    @Override
    public GlobalPublicationStatistics getGlobalPublicationStatistics() {

        GlobalPublicationStatistics globalPublicationStatistics = new GlobalPublicationStatistics();

        globalPublicationService.findAllPublicationIds().forEach(pubId -> {

            List<EntryPublication> entryPublications = publicationService.getEntryPublications(pubId);

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
