package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.publication.GlobalPublicationStatistics;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public interface StatisticsService {

    enum Counter {
        ENTRY_COUNT(Collections.singleton("MASTER")),
        ISOFORM_COUNT(Collections.singleton("ISOFORM")),
        VARIANT_COUNT(Collections.singleton("PROTEIN_SEQUENCE_VARIANT")),
        PTM_COUNT(Collections.singleton("PROTEIN_PTM")),
        MISSING_PROTEIN_COUNT(new HashSet<>(Arrays.asList("TRANSCRIPT_LEVEL_MASTER", "HOMOLOGY_MASTER", "PREDICTED_MASTER")));
        // TODO add PROTEIN_WITH_NO_FUNCTION_ANNOTATED_COUNT when it's added into release-stats and see StatisticsServiceImpl.getStatsByPlaceholder()

        Set<String> dbTags;

        Counter(Set<String> dbTags) {
            this.dbTags = dbTags;
        }

        /**
         * Get tags which are the values of the columns of the view 'stats_view'.
         * If there are several tags, that means the count should be the sum of the value associated to them.
         */
        public Set<String> getDbTags() {
            return dbTags;
        }
    }

    GlobalPublicationStatistics getGlobalPublicationStatistics();

    Map<Counter, Integer> getStatsByPlaceholder(Set<Counter> stat);
}
