package org.nextprot.api.core.service.impl;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.publication.EntryPublication;
import org.nextprot.api.core.domain.publication.GlobalPublicationStatistics;
import org.nextprot.api.core.domain.release.ReleaseStatsTag;
import org.nextprot.api.core.service.GlobalPublicationService;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.api.core.service.ReleaseInfoService;
import org.nextprot.api.core.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private GlobalPublicationService globalPublicationService;

    @Autowired
    private ReleaseInfoService releaseInfoService;

    @Cacheable(value = "global-publication-statistics", sync = true)
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

    @Override
    public Map<Counter, Integer> getStatsByPlaceholder(Set<Counter> stat) {

        final List<ReleaseStatsTag> releaseStatsTags = releaseInfoService.findReleaseStats().getTagStatistics();

        return stat.stream().collect(Collectors.toMap(Function.identity(), s -> sumTagCounts(releaseStatsTags, s.getDbTags())));
    }

    private int sumTagCounts(final List<ReleaseStatsTag> releaseStatsTags, final Set<String> statNames) {
        Set<ReleaseStatsTag> result = releaseStatsTags.stream()
                                                      .filter(s -> statNames.contains(s.getTag()))
                                                      .collect(Collectors.toSet());
        if (result.size() > 0) {
            return result.stream()
                         .map(ReleaseStatsTag::getCount)
                         .mapToInt(Integer::intValue)
                         .sum();
        }
        throw new NextProtException("Found no count for the tags " + statNames);
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
