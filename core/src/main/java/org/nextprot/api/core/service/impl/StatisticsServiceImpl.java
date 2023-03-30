package org.nextprot.api.core.service.impl;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.EntryReportStats;
import org.nextprot.api.core.domain.GlobalEntryStatistics;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.publication.EntryPublication;
import org.nextprot.api.core.domain.publication.GlobalPublicationStatistics;
import org.nextprot.api.core.domain.release.ReleaseStatsTag;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.core.service.EntryReportStatsService;
import org.nextprot.api.core.service.GlobalPublicationService;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.api.core.service.ReleaseInfoService;
import org.nextprot.api.core.service.StatisticsService;
import org.nextprot.api.core.service.TerminologyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.nextprot.api.commons.constants.AnnotationCategory.*;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private GlobalPublicationService globalPublicationService;

    @Autowired
    private AnnotationService annotationService;

    @Autowired
    EntryReportStatsService entryReportStatsService;

    @Autowired
    private MasterIdentifierService masterIdentifierService;

    @Autowired
    private ReleaseInfoService releaseInfoService;

    @Autowired
    private TerminologyService terminologyService;

    @Cacheable(value = "global-entry-statistics", sync = true)
    @Override
    public GlobalEntryStatistics getGlobalEntryStatistics() {

        GlobalEntryStatistics globalEntryStatistics = new GlobalEntryStatistics();
        Set<String> distinctInteractions = new HashSet<>();

        masterIdentifierService.findUniqueNames().forEach(uniqueName -> {

            List<Annotation> annotations = annotationService.findAnnotations(uniqueName);
            EntryReportStats entryReportStats = entryReportStatsService.reportEntryStats(uniqueName);

            // Count number of annotations with a non empty term
            globalEntryStatistics.incrementNumberOfEntryTermLink(
            	(int)annotations.stream().filter(a -> a.getCvTermAccessionCode() != null && a.getCvTermAccessionCode().length()>0).count()
            );
            
            // Count number of entries with expression profile
            if (entryReportStats.isExpression()) {
                globalEntryStatistics.incrementNumberOfEntriesWithExpressionProfile();
            }

            // Count number of entries with disease
            if (entryReportStats.isDisease()) {
                globalEntryStatistics.incrementNumberOfEntriesWithDisease();
            }

            // Count number of variants
            globalEntryStatistics.incrementNumberOfVariants(entryReportStats.countVariants());

            // Get distinct interactions to be counted at the end (defined as interactant1::interactant2)
            Set<String> interactants = annotations.stream()
                                             .filter(a -> a.getAPICategory().equals(BINARY_INTERACTION))
                                             .map(a -> a.getBioObject().getAccession())
                                             .collect(Collectors.toSet());
            for (String interactant : interactants) {
                // We sort interactants to not count reverse interactions such as interactant2::interactant1
                distinctInteractions.add(Stream.of(interactant, uniqueName).sorted().collect(Collectors.joining("::")));
            }
        });
        globalEntryStatistics.setNumberOfEntriesWithBinaryInteraction(distinctInteractions.size());

        return globalEntryStatistics;
    }

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
    public int getCvTermCount() {
        return terminologyService.findAllCVTerms().size();
    }

    @Override
    public Map<Counter, Integer> getStatsByPlaceholder(Set<Counter> stat) {

        final List<ReleaseStatsTag> releaseStatsTags = releaseInfoService.findReleaseStats().getTagStatistics();

        Map<Counter, Integer> map = stat.stream()
                                            .filter(c -> !c.getDbTags().isEmpty())
                                            .collect(Collectors.toMap(
                                                    Function.identity(),
                                                    s -> sumTagCounts(releaseStatsTags, s.getDbTags())));
        map.put(Counter.PROTEIN_WITH_NO_FUNCTION_ANNOTATED_COUNT, releaseInfoService.getAnnotationWithoutFunctionTag().getCount());
        return map;
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
