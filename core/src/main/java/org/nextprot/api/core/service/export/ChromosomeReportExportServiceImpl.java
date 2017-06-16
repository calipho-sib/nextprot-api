package org.nextprot.api.core.service.export;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.ChromosomeReport;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.service.*;
import org.nextprot.api.core.service.export.format.NextprotMediaType;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.commons.constants.QualityQualifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ChromosomeReportExportServiceImpl implements ChromosomeReportExportService {

	static String NACETYLATION_REG_EXP = "^N.*?-acetyl.+$";
	static String PHOSPHORYLATION_REG_EXP = "^Phospho.*$";

	@Autowired
	private ChromosomeReportService chromosomeReportService;
	@Autowired
	private ChromosomeReportSummaryService chromosomeReportSummaryService;
	@Autowired
	private OverviewService overviewService;
	@Autowired
	private MasterIdentifierService masterIdentifierService;
	@Autowired
	private EntryBuilderService entryBuilderService;
	@Autowired
	private AnnotationService annotationService;

	@Override
	public void exportChromosomeEntryReport(String chromosome, NextprotMediaType nextprotMediaType, OutputStream os) throws IOException {

		Optional<ChromosomeReportWriter> writer = ChromosomeReportWriter.valueOf(nextprotMediaType, os);

		if (writer.isPresent()) {
			writer.get().write(chromosomeReportService.reportChromosome(chromosome));
		}
		else {
			throw new NextProtException("cannot export chromosome "+chromosome+": " + "unsupported "+nextprotMediaType+" format");
		}
	}

	@Override
	public void exportHPPChromosomeEntryReport(String chromosome, NextprotMediaType nextprotMediaType, OutputStream os) throws IOException {

		Optional<HPPChromosomeReportWriter> writer = HPPChromosomeReportWriter.valueOf(nextprotMediaType, os, overviewService);

		if (writer.isPresent()) {
			writer.get().write(chromosomeReportService.reportChromosome(chromosome));
		}
		else {
			throw new NextProtException("cannot export hpp chromosome "+chromosome+": " + "unsupported "+nextprotMediaType+" format");
		}
	}

	@Override
	public void exportHPPChromosomeEntryReportCountByProteinExistence(OutputStream os) throws IOException {

		PrintWriter writer = new PrintWriter(os);

		Map<String, ChromosomeReport.Summary> summaries = chromosomeReportSummaryService.getChromosomeSummaries();

		writer.write(Stream.of(
				"chromosome",
				"entry count",
				"protein level (PE1)",
				"transcript level (PE2)",
				"homology (PE3)",
				"predicted (PE4)",
				"uncertain (PE5)",
				"awaiting protein validation (P2+P3+P4)"
		).collect(Collectors.joining("\t")));

		writer.write("\n");

		for (String chromosome : ChromosomeReportService.getChromosomeNames()) {

			ChromosomeReport.Summary summary = summaries.get(chromosome);

			writer.write(Stream.of(
					chromosome,
					String.valueOf(summary.getEntryCount()),
					String.valueOf(summary.getProteinLevelEntryCount()),
					String.valueOf(summary.getTranscriptLevelEntryCount()),
					String.valueOf(summary.getHomologyLevelEntryCount()),
					String.valueOf(summary.getPredictedLevelEntryCount()),
					String.valueOf(summary.getUncertainLevelEntryCount()),
					String.valueOf(summary.getTranscriptLevelEntryCount()+summary.getHomologyLevelEntryCount()+summary.getPredictedLevelEntryCount())
			).collect(Collectors.joining("\t")));

			writer.write("\n");
		}

		writer.close();
	}

	@Override
	public void exportNAcetylatedEntries(OutputStream os) throws IOException {

		exportPtmEntries(NACETYLATION_REG_EXP, os);
	}

	@Override
	public void exportPhosphorylatedEntries(OutputStream os) throws IOException {

		exportPtmEntries(PHOSPHORYLATION_REG_EXP, os);
	}

	private void exportPtmEntries(String ptmRegExp, OutputStream os) throws IOException {

		PrintWriter writer = new PrintWriter(os);

		writer.write(Stream.of("chromosome", "accession")
				.collect(Collectors.joining("\t")));
		writer.write("\n");

		for (String chromosome : Arrays.asList("MT")) {

			masterIdentifierService.findUniqueNamesOfChromosome(chromosome).stream()
					.map(acc -> entryBuilderService.build(EntryConfig.newConfig(acc).withAnnotations()))
					.filter(e -> containsPtmAnnotation(e, ptmRegExp, annotationService))
					.sorted()
					.forEach(e -> {
						writer.write(chromosome);
						writer.write("\t");
						writer.write(e.getUniqueName());
						writer.write("\n");
					});
		}

		writer.close();
	}

	static boolean containsPtmAnnotation(Entry entry, String ptmRegExp, AnnotationService annotationService) {

		List<Annotation> ptms = entry.getAnnotationsByCategory()
				.get(StringUtils.camelToKebabCase(AnnotationCategory.MODIFIED_RESIDUE.getApiTypeName()));

		Predicate<AnnotationEvidence> isDescendantPredicate = annotationService.createDescendantEvidenceTermPredicate("ECO:0000006");

		return nullableListToStream(ptms)
				.anyMatch(annot -> annot.getQualityQualifier().equals(QualityQualifier.GOLD.name()) &&
                        annot.getCvTermName().matches(ptmRegExp) &&
                        annot.getEvidences().stream()
                            .anyMatch(evi -> evi.getQualityQualifier().equals(QualityQualifier.GOLD.name())
									&& isDescendantPredicate.test(evi))
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
