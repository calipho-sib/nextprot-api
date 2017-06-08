package org.nextprot.api.core.service.impl;

import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.domain.ChromosomeReport;
import org.nextprot.api.core.domain.EntryReport;
import org.nextprot.api.core.service.ChromosomeReportService;
import org.nextprot.api.core.service.EntryReportService;
import org.nextprot.api.core.service.ReleaseInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChromosomeReportServiceImpl implements ChromosomeReportService {

	@Autowired
	private MasterIdentifierService masterIdentifierService;

	@Autowired
	private EntryReportService entryReportService;

	@Autowired
	private ReleaseInfoService releaseInfoService;

	@Cacheable("chromosome-reports")
	@Override
	public ChromosomeReport reportChromosome(String chromosome) {

		if (!ChromosomeReportService.getChromosomeNames().contains(chromosome)) {
			throw new ChromosomeNotFoundException(chromosome, ChromosomeReportService.getChromosomeNames().toString());
		}

		ChromosomeReport report = new ChromosomeReport();

        report.setDataRelease(releaseInfoService.findReleaseInfo().getDatabaseRelease());

		List<EntryReport> entryReports = masterIdentifierService.findUniqueNamesOfChromosome(chromosome).stream()
				.map(entryAccession -> entryReportService.reportEntry(entryAccession))
				.flatMap(Collection::stream)
				.filter(er -> er.getChromosome().equals(chromosome))
				.sorted((er1, er2) -> new EntryReport.ByGenePosComparator().compare(er1, er2))
				.collect(Collectors.toList());

		report.setEntryReports(entryReports);
		report.setSummary(newSummary(chromosome, entryReports));

		return report;
	}

	private ChromosomeReport.Summary newSummary(String chromosome, List<EntryReport> entryReports) {

		ChromosomeReport.Summary summary = new ChromosomeReport.Summary();

		summary.setChromosome(chromosome);

		summary.setEntryCount((int) entryReports.stream()
				.map(EntryReport::getAccession)
				.distinct()
				.count());
		summary.setEntryReportCount(entryReports.size());

		return summary;
	}
}
