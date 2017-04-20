package org.nextprot.api.core.service.impl;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.domain.ChromosomeReport;
import org.nextprot.api.core.domain.EntryReport;
import org.nextprot.api.core.service.ChromosomeReportService;
import org.nextprot.api.core.service.EntryReportService;
import org.nextprot.api.core.service.export.ChromosomeReportWriter;
import org.nextprot.api.core.service.export.format.NextprotMediaType;
import org.nextprot.api.core.service.export.writer.ChromosomeReportTXTWriter;
import org.nextprot.api.core.service.export.writer.ChromosomeReportTSVWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChromosomeReportServiceImpl implements ChromosomeReportService {

	@Autowired
	private MasterIdentifierService masterIdentifierService;

	@Autowired
	private EntryReportService entryReportService;

	@Cacheable("chromosome-reports")
	@Override
	public ChromosomeReport reportChromosome(String chromosome) {

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

	@Override
	public void exportChromosomeEntryReport(String chromosome, NextprotMediaType nextprotMediaType, HttpServletResponse response) {

		try (OutputStream os = response.getOutputStream()) {
			ChromosomeReportWriter writer;

			String filename = "chromosome" + chromosome + "." + nextprotMediaType.getExtension();
			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

			if (nextprotMediaType == NextprotMediaType.TSV) {
				writer = new ChromosomeReportTSVWriter(os);
			}
			else if (nextprotMediaType == NextprotMediaType.TXT) {
				writer = new ChromosomeReportTXTWriter(os);
			}
			else {
				throw new NextProtException("cannot export chromosome "+chromosome+": " + "unsupported "+nextprotMediaType+" format");
			}

			writer.write(reportChromosome(chromosome));
		} catch (IOException e) {
			throw new NextProtException("cannot export chromosome "+chromosome+" as "+ nextprotMediaType);
		}
	}

	private ChromosomeReport.Summary newSummary(String chromosome, List<EntryReport> entryReports) {

		ChromosomeReport.Summary summary = new ChromosomeReport.Summary();

		summary.setChromosome(chromosome);
		summary.setEntryCount((int) entryReports.stream()
				.map(EntryReport::getAccession)
				.distinct()
				.count());
		summary.setGeneCount((int) entryReports.stream()
				.map(EntryReport::getGeneName)
				.distinct()
				.count());

		return summary;
	}
}
