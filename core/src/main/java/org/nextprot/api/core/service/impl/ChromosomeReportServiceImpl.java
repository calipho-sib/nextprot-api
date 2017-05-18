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
import java.util.Map;
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

		if (!getChromosomeNames().contains(chromosome)) {
			throw new ChromosomeNotFoundException(chromosome, getChromosomeNames().toString());
		}

		ChromosomeReport report = new ChromosomeReport();

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

	@Cacheable("chromosomes")
	@Override
	public Map<String, ChromosomeReport.Summary.Count> getChromosomeCounts() {

		return getChromosomeNames().stream()
				.collect(Collectors.toMap(
						k -> k,
						k -> reportChromosome(k).getSummary().getCount(),
						(k1, k2) -> k1));
	}

	private ChromosomeReport.Summary newSummary(String chromosome, List<EntryReport> entryReports) {

		ChromosomeReport.Summary summary = new ChromosomeReport.Summary();

		summary.setChromosome(chromosome);
		summary.setDataRelease(releaseInfoService.findReleaseInfo().getDatabaseRelease());
		summary.setCount(newCounts(entryReports));

		return summary;
	}

	private ChromosomeReport.Summary.Count newCounts(List<EntryReport> entryReports) {

		ChromosomeReport.Summary.Count count = new ChromosomeReport.Summary.Count();
		count.setEntryCount((int) entryReports.stream()
				.map(EntryReport::getAccession)
				.distinct()
				.count());
		count.setGeneCount((int) entryReports.stream()
				.map(er -> er.getGeneName()+er.getCodingStrand())
				.distinct()
				.count());
		return count;
	}
}
