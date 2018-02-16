package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.EntryReportStats;

import java.util.Map;

public interface EntryReportStatsService {

	EntryReportStats reportEntryStats(String entryAccession);

	Map<String, String> reportIsoformPeffHeaders(String entryAccession);
}
