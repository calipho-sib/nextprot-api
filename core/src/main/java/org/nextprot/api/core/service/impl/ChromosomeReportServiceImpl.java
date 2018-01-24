package org.nextprot.api.core.service.impl;

import org.nextprot.api.commons.bio.Chromosome;
import org.nextprot.api.commons.exception.ChromosomeNotFoundException;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.domain.ChromosomeReport;
import org.nextprot.api.core.domain.EntryReport;
import org.nextprot.api.core.domain.ProteinExistence;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;
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
	private AnnotationService annotationService;

    @Autowired
    private ProteinExistenceService proteinExistenceService;

    @Autowired
	private ProteinExistenceInferenceService proteinExistenceInferenceService;

	@Cacheable("chromosome-reports")
	@Override
	public ChromosomeReport reportChromosome(String chromosome) {

		// TODO: if chromosome is not found throw an http error 404
		if (!Chromosome.exists(chromosome)) {
			throw new ChromosomeNotFoundException(chromosome);
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
				.filter(acc -> entryReportService.isEntryNAcetyled(acc, isExperimentalPredicate))
				.sorted()
				.collect(Collectors.toList());

	}

	@Cacheable("phospho-master-unique-names-by-chromosome")
	@Override
	public List<String> findPhosphorylatedEntries(String chromosome) {

		Predicate<AnnotationEvidence> isExperimentalPredicate = annotationService.createDescendantEvidenceTermPredicate("ECO:0000006");
		
		return masterIdentifierService.findUniqueNamesOfChromosome(chromosome).stream()
				.filter(acc -> entryReportService.isEntryPhosphorylated(acc, isExperimentalPredicate))
				.sorted()
				.collect(Collectors.toList());
	}

	@Cacheable("unconfirmed-ms-master-unique-names-by-chromosome")
	@Override
	public List<String> findUnconfirmedMsDataEntries(String chromosome) {

        return masterIdentifierService.findUniqueNamesOfChromosome(chromosome).stream()
				.filter(acc -> proteinExistenceInferenceService.inferProteinExistence(acc).isInferenceFound())
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

		Map<ProteinExistence, List<String>> pe2entries = new EnumMap<>(ProteinExistence.class);

        for (String entry : chromosomeEntries) {

			ProteinExistence pe = proteinExistenceService.getProteinExistences(entry).getInferredProteinExistence();

			if (!pe2entries.containsKey(pe)) {

				pe2entries.put(pe, new ArrayList<>());
			}

			pe2entries.get(pe).add(entry);
		}

		pe2entries.forEach((key, value) -> summary.setEntryCount(key, value.size()));
	}


}
