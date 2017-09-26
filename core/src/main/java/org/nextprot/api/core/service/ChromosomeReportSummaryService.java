package org.nextprot.api.core.service;

import org.nextprot.api.commons.bio.Chromosome;
import org.nextprot.api.core.domain.ChromosomeReport;

import java.util.Map;
import java.util.stream.Collectors;

public interface ChromosomeReportSummaryService {

	/**
	 * Report chromosome summary informations
	 * @param chromosome the chromosome to get report
	 * @return a pojo containing the report
	 */
	ChromosomeReport.Summary reportChromosomeSummary(String chromosome);

	default Map<String, ChromosomeReport.Summary> getChromosomeSummaries() {

		return Chromosome.getNames().stream()
				.collect(Collectors.toMap(
						k -> k,
						this::reportChromosomeSummary,
						(k1, k2) -> k1));
	}
}
