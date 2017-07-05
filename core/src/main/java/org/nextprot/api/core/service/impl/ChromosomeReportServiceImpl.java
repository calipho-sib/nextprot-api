package org.nextprot.api.core.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.domain.ChromosomeReport;
import org.nextprot.api.core.domain.EntryReport;
import org.nextprot.api.core.domain.EntryUtils;
import org.nextprot.api.core.domain.Overview;
import org.nextprot.api.core.domain.ProteinExistenceLevel;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.core.service.ChromosomeReportService;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.EntryReportService;
import org.nextprot.api.core.service.OverviewService;
import org.nextprot.api.core.service.ReleaseInfoService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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

	@Autowired
	private EntryBuilderService entryBuilderService;

	@Autowired
	private AnnotationService annotationService;

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
				.sorted(EntryReport.newByChromosomalPositionComparator())
				.collect(Collectors.toList());

		report.setEntryReports(entryReports);

		ChromosomeReport.Summary summary = newSummary(chromosome, entryReports);
		setByProteinEvidenceEntryCount(allEntriesOnChromosome, summary);

		report.setSummary(summary);

		return report;
	}

	@Cacheable("nacetylated-master-unique-names-by-chromosome")
	@Override
	public List<String> findNAcetylatedEntries(String chromosome) {

		Predicate<AnnotationEvidence> isExperimentalPredicate = annotationService.createDescendantEvidenceTermPredicate("ECO:0000006");
		
		return masterIdentifierService.findUniqueNamesOfChromosome(chromosome).stream()
				.filter(acc -> 
					entryReportService.entryIsNAcetyled(
						entryBuilderService.build(EntryConfig.newConfig(acc).withAnnotations()), 
						isExperimentalPredicate)
					)
				.sorted()
				.collect(Collectors.toList());

	}

	@Cacheable("phospho-master-unique-names-by-chromosome")
	@Override
	public List<String> findPhosphorylatedEntries(String chromosome) {

		Predicate<AnnotationEvidence> isExperimentalPredicate = annotationService.createDescendantEvidenceTermPredicate("ECO:0000006");
		
		return masterIdentifierService.findUniqueNamesOfChromosome(chromosome).stream()
				.filter(acc -> 
					entryReportService.entryIsPhosphorylated(
						entryBuilderService.build(EntryConfig.newConfig(acc).withAnnotations()), 
						isExperimentalPredicate)
					)
				.sorted()
				.collect(Collectors.toList());
	}

	@Cacheable("unconfirmed-ms-master-unique-names-by-chromosome")
	@Override
	public List<String> findUnconfirmedMsDataEntries(String chromosome) {

		return masterIdentifierService.findUniqueNamesOfChromosome(chromosome).stream()
				.filter(acc -> EntryUtils.wouldUpgradeToPE1AccordingToOldRule(entryBuilderService.build(EntryConfig.newConfig(acc).withEverything())))
				.collect(Collectors.toList());
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

	private void setByProteinEvidenceEntryCount(List<String> chromosomeEntries, ChromosomeReport.Summary summary) {

		Map<ProteinExistenceLevel, List<String>> pe2entries = new HashMap<>();

		for (String entry : chromosomeEntries) {

			Overview overview = overviewService.findOverviewByEntry(entry);

			ProteinExistenceLevel level = ProteinExistenceLevel.valueOfLevel(overview.getProteinExistenceLevel());

			if (!pe2entries.containsKey(level)) {

				pe2entries.put(level, new ArrayList<>());
			}

			pe2entries.get(level).add(entry);
		}

		pe2entries.forEach((key, value) -> summary.setEntryCount(key, value.size()));
	}


}
