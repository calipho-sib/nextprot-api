package org.nextprot.api.core.service.impl;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.domain.EntryReport;
import org.nextprot.api.core.service.ChromosomeReportService;
import org.nextprot.api.core.service.EntryReportService;
import org.nextprot.api.core.service.export.writer.EntryReportJSONWriter;
import org.nextprot.api.core.service.export.writer.EntryReportTSVWriter;
import org.nextprot.api.core.service.export.EntryReportWriter;
import org.nextprot.api.core.service.export.format.FileFormat;
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
	public void exportChromosomeEntryReport(String chromosome, FileFormat fileFormat, HttpServletResponse response) {

		try (OutputStream os = response.getOutputStream()) {
			EntryReportWriter writer;

			String filename = "chromosome" + chromosome + "." + fileFormat.getExtension();
			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

			if (fileFormat == FileFormat.TSV) {
				writer = new EntryReportTSVWriter(os);
			}
			else if (fileFormat == FileFormat.JSON) {
				writer = new EntryReportJSONWriter(os);
			}
			else {
				throw new NextProtException(fileFormat+ ": unknown format");
			}

			writer.write(exportChromosomeEntryReport(chromosome));
		} catch (IOException e) {
			throw new NextProtException("cannot export chromosome "+chromosome+" in "+fileFormat);
		}
	}

	private List<EntryReport> exportChromosomeEntryReport(String chromosome) {

		return masterIdentifierService.findUniqueNamesOfChromosome(chromosome).stream()
				.map(entryAccession -> entryReportService.reportEntry(entryAccession))
				.flatMap(Collection::stream)
				.filter(er -> er.getChromosome().equals(chromosome))
				.sorted((er1, er2) -> new EntryReport.ByGenePosComparator().compare(er1, er2))
				.collect(Collectors.toList());
	}
}
