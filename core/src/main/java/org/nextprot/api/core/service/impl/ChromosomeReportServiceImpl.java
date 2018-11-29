package org.nextprot.api.core.service.impl;

import org.nextprot.api.commons.bio.Chromosome;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.ChromosomeNotFoundException;
import org.nextprot.api.core.domain.ChromosomeReport;
import org.nextprot.api.core.domain.EntryReport;
import org.nextprot.api.core.domain.ProteinExistence;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.core.service.ChromosomeReportService;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.EntryGeneReportService;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.core.service.OverviewService;
import org.nextprot.api.core.service.ReleaseInfoService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.utils.EntryUtils;
import org.nextprot.commons.constants.QualityQualifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.nextprot.api.commons.utils.StreamUtils.nullableListToStream;

@Service
public class ChromosomeReportServiceImpl implements ChromosomeReportService {

	private static String NACETYLATION_REG_EXP = "^N.*?-acetyl.+$";
	private static String PHOSPHORYLATION_REG_EXP = "^Phospho.*$";

	@Autowired
	private MasterIdentifierService masterIdentifierService;

	@Autowired
	private EntryGeneReportService entryGeneReportService;

	@Autowired
	private ReleaseInfoService releaseInfoService;

	@Autowired
	private AnnotationService annotationService;

    @Autowired
    private EntryBuilderService entryBuilderService;

    @Autowired
	private OverviewService overviewService;

	@Cacheable("chromosome-reports")
	@Override
	public ChromosomeReport reportChromosome(String chromosome) {

		// TODO: if chromosome is not found throw an http error 404
		if (!Chromosome.exists(chromosome)) {
			throw new ChromosomeNotFoundException(chromosome);
		}

		ChromosomeReport report = new ChromosomeReport();

        report.setDataRelease(releaseInfoService.findReleaseVersions().getDatabaseRelease());

		List<String> allEntriesOnChromosome = masterIdentifierService.findUniqueNamesOfChromosome(chromosome);

		List<EntryReport> entryReports = allEntriesOnChromosome.stream()
				.map(entryAccession -> entryGeneReportService.reportEntry(entryAccession))
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

		return masterIdentifierService.findUniqueNamesOfChromosome(chromosome).stream()
				.filter(acc -> containsPtmAnnotation(acc, NACETYLATION_REG_EXP))
				.sorted()
				.collect(Collectors.toList());

	}

	@Cacheable("phospho-master-unique-names-by-chromosome")
	@Override
	public List<String> findPhosphorylatedEntries(String chromosome) {

		return masterIdentifierService.findUniqueNamesOfChromosome(chromosome).stream()
				.filter(acc -> containsPtmAnnotation(acc, PHOSPHORYLATION_REG_EXP))
				.sorted()
				.collect(Collectors.toList());
	}

	@Cacheable("unconfirmed-ms-master-unique-names-by-chromosome")
	@Override
	public List<String> findUnconfirmedMsDataEntries(String chromosome) {

        return masterIdentifierService.findUniqueNamesOfChromosome(chromosome).stream()
				.filter(acc -> EntryUtils.isUnconfirmedMS(
						entryBuilderService.build(EntryConfig.newConfig(acc).withAnnotations().withOverview())))
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

			ProteinExistence pe = overviewService.findOverviewByEntry(entry).getProteinExistence();

			if (!pe2entries.containsKey(pe)) {

				pe2entries.put(pe, new ArrayList<>());
			}

			pe2entries.get(pe).add(entry);
		}

		pe2entries.forEach((key, value) -> summary.setEntryCount(key, value.size()));
	}

	private boolean containsPtmAnnotation(String entryName, String ptmRegExp) {

		List<Annotation> annotations = annotationService.findAnnotations(entryName);

		Predicate<AnnotationEvidence> isExperimentalPredicate = annotationService.createDescendantEvidenceTermPredicate("ECO:0000006");

		List<Annotation> ptms = annotations.stream()
				.filter(annotation -> annotation.getAPICategory() == AnnotationCategory.MODIFIED_RESIDUE)
				.collect(Collectors.toList());

		return nullableListToStream(ptms)
				.anyMatch(annot -> isGoldAnnotation(annot) &&
						annotationTermMatchesPattern(annot, ptmRegExp) &&
						annot.getEvidences().stream()
								.anyMatch(evi -> isGoldEvidence(evi) && isExperimentalEvidence(evi,isExperimentalPredicate)
								)
				);
	}

	private boolean isGoldAnnotation(Annotation annot) {
		boolean result = annot.getQualityQualifier().equals(QualityQualifier.GOLD.name());
		//System.out.println("annot " + annot.getAnnotationId() + " quality: " + annot.getQualityQualifier());
		return result;
	}

	private boolean annotationTermMatchesPattern(Annotation annot, String ptmRegExp) {
		boolean result = annot.getCvTermName().matches(ptmRegExp);
		//System.out.println("annot " + annot.getAnnotationId() + " matches " + ptmRegExp +": " + result);
		return result;
	}

	private boolean isGoldEvidence(AnnotationEvidence evi) {
		boolean result = evi.getQualityQualifier().equals(QualityQualifier.GOLD.name());
		//System.out.println("annot " + evi.getAnnotationId() +  " evi " + evi.getEvidenceId() + " quality: " + evi.getQualityQualifier());
		return result;
	}

	private boolean isExperimentalEvidence(AnnotationEvidence evi, Predicate<AnnotationEvidence> isExperimentalPredicate) {
		boolean result = isExperimentalPredicate.test(evi);
		//System.out.println("annot " + evi.getAnnotationId() +  " evi " + evi.getEvidenceId() + " experimental: " + result);
		return result;
	}
}
