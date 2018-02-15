package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.EntryReportStats;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;

import java.util.Map;
import java.util.function.Predicate;

public interface EntryReportStatsService {

	EntryReportStats reportEntryStats(String entryAccession);

	boolean isEntryNAcetyled(String entryAccession, Predicate<AnnotationEvidence> isExperimentalPredicate);

	boolean isEntryPhosphorylated(String entryAccession, Predicate<AnnotationEvidence> isExperimentalPredicate);

	Map<String, String> reportIsoformPeffHeaders(String entryAccession);
}
