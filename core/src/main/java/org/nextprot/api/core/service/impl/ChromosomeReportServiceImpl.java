package org.nextprot.api.core.service.impl;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.*;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.service.*;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.commons.constants.QualityQualifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ChromosomeReportServiceImpl implements ChromosomeReportService {

	static String NACETYLATION_REG_EXP = "^N.*?-acetyl.+$";
	static String PHOSPHORYLATION_REG_EXP = "^Phospho.*$";

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

		return listPtmEntries(chromosome, NACETYLATION_REG_EXP);
	}

	@Cacheable("phospho-master-unique-names-by-chromosome")
	@Override
	public List<String> findPhosphorylatedEntries(String chromosome) {

		return listPtmEntries(chromosome, PHOSPHORYLATION_REG_EXP);
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

	private List<String> listPtmEntries(String chromosome, String ptmRegExp) {

		Predicate<AnnotationEvidence> isExperimentalPredicate = annotationService.createDescendantEvidenceTermPredicate("ECO:0000006");

		return masterIdentifierService.findUniqueNamesOfChromosome(chromosome).stream()
				.filter(acc -> containsPtmAnnotation(
						entryBuilderService.build(EntryConfig.newConfig(acc).withAnnotations()),
						ptmRegExp, isExperimentalPredicate))
				.sorted()
				.collect(Collectors.toList());
	}

	boolean containsPtmAnnotation(Entry entry, String ptmRegExp, Predicate<AnnotationEvidence> isExperimentalPredicate) {

		List<Annotation> ptms = entry.getAnnotationsByCategory()
				.get(StringUtils.camelToKebabCase(AnnotationCategory.MODIFIED_RESIDUE.getApiTypeName()));

		return nullableListToStream(ptms)
				.anyMatch(annot -> annot.getQualityQualifier().equals(QualityQualifier.GOLD.name()) &&
						annot.getCvTermName().matches(ptmRegExp) &&
						annot.getEvidences().stream()
								.anyMatch(evi -> evi.getQualityQualifier().equals(QualityQualifier.GOLD.name())
										&& isExperimentalPredicate.test(evi))
				);
	}

	/**
	 * Return a stream from a nullable list
	 * @param list the list to stream
	 * @param <T> element type
	 * @return a Stream
	 */
	private static <T> Stream<T> nullableListToStream(List<T> list) {

		return list == null ? Stream.empty() : list.stream();
	}
}
