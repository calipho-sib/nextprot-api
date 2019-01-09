package org.nextprot.api.core.service.impl;

import org.nextprot.api.core.domain.ChromosomeReport;
import org.nextprot.api.core.service.ChromosomeReportService;
import org.nextprot.api.core.service.ChromosomeReportSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ChromosomeReportSummaryServiceImpl implements ChromosomeReportSummaryService {

	@Autowired
	private ChromosomeReportService chromosomeReportService;

	@Cacheable(value = "chromosome-summaries", sync = true)
	@Override
	public ChromosomeReport.Summary reportChromosomeSummary(String chromosome) {

		return chromosomeReportService.reportChromosome(chromosome).getSummary();
	}
}
