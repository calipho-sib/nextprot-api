package org.nextprot.api.core.service.impl;

import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.domain.ChromosomeReport;
import org.nextprot.api.core.domain.EntryReport;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.core.domain.ProteinExistenceLevel;
import org.nextprot.api.core.service.ChromosomeReportService;
import org.nextprot.api.core.service.EntryReportService;
import org.nextprot.api.core.service.OverviewService;
import org.nextprot.api.core.service.ReleaseInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChromosomeReportServiceImpl implements ChromosomeReportService {

	@Autowired
	private MasterIdentifierService masterIdentifierService;

	@Autowired
	private EntryReportService entryReportService;

	@Autowired
	private ReleaseInfoService releaseInfoService;

	@Autowired
	private OverviewService overviewService;

	@Cacheable("chromosome-reports")
	@Override
	public ChromosomeReport reportChromosome(String chromosome) {

		if (!ChromosomeReportService.getChromosomeNames().contains(chromosome)) {
			throw new ChromosomeNotFoundException(chromosome, ChromosomeReportService.getChromosomeNames().toString());
		}

		ChromosomeReport report = new ChromosomeReport();

        report.setDataRelease(releaseInfoService.findReleaseInfo().getDatabaseRelease());

		List<String> allEntriesOnChromosome = masterIdentifierService.findUniqueNamesOfChromosome(chromosome);

		List<EntryReport> entryReports = allEntriesOnChromosome.stream()
				.map(entryAccession -> entryReportService.reportEntry(entryAccession))
				.flatMap(Collection::stream)
				.filter(er -> er.getChromosome().equals(chromosome))
				.sorted((er1, er2) -> new EntryReport.ByGenePosComparator().compare(er1, er2))
				.collect(Collectors.toList());

		report.setEntryReports(entryReports);
		report.setSummary(newSummary(chromosome, entryReports));
		report.setEntryCountByProteinExistence(newChromosomeReportByProteinEvidence(allEntriesOnChromosome));

		return report;
	}

	@Cacheable("chromosome-summaries")
	@Override
	public Map<String, ChromosomeReport.Summary> getChromosomeSummaries() {

		return ChromosomeReportService.getChromosomeNames().stream()
				.collect(Collectors.toMap(
						k -> k,
						k -> reportChromosome(k).getSummary(),
						(k1, k2) -> k1));
	}

	@Cacheable("chromosome-entry-count-by-pe")
	@Override
	public Map<String, ChromosomeReport.EntryCountByProteinExistence> getChromosomeEntryCountByProteinExistence() {

		return ChromosomeReportService.getChromosomeNames().stream()
				.collect(Collectors.toMap(
						k -> k,
						k -> reportChromosome(k).getEntryCountByProteinExistence(),
						(k1, k2) -> k1));
	}

	private ChromosomeReport.Summary newSummary(String chromosome, List<EntryReport> entryReports) {

		ChromosomeReport.Summary summary = new ChromosomeReport.Summary();

		summary.setChromosome(chromosome);

		summary.setEntryCount((int) entryReports.stream()
				.map(EntryReport::getAccession)
				.distinct()
				.count());
		summary.setGeneCount((int) entryReports.stream()
				.map(er -> er.getGeneName()+er.getCodingStrand())
				.distinct()
				.count());

		return summary;
	}

	private ChromosomeReport.EntryCountByProteinExistence newChromosomeReportByProteinEvidence(List<String> chromosomeEntries) {

		ChromosomeReport.EntryCountByProteinExistence report = new ChromosomeReport.EntryCountByProteinExistence();

		Map<ProteinExistenceLevel, List<String>> pe2entries = new HashMap<>();

		for (String entry : chromosomeEntries) {

			Overview overview = overviewService.findOverviewByEntry(entry);

			ProteinExistenceLevel level = ProteinExistenceLevel.valueOfLevel(overview.getProteinExistenceLevel());

			if (!pe2entries.containsKey(level)) {

				pe2entries.put(level, new ArrayList<>());
			}

			pe2entries.get(level).add(entry);
		}

		pe2entries.forEach((key, value) -> report.setEntryCount(key, value.size()));

		return report;
	}
}
