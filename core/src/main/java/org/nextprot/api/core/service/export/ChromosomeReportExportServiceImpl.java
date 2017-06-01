package org.nextprot.api.core.service.export;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.service.ChromosomeReportExportService;
import org.nextprot.api.core.service.ChromosomeReportService;
import org.nextprot.api.core.service.OverviewService;
import org.nextprot.api.core.service.export.format.NextprotMediaType;
import org.nextprot.api.core.service.export.io.HPPChromosomeReportTXTWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

@Service
public class ChromosomeReportExportServiceImpl implements ChromosomeReportExportService {

	@Autowired
	private ChromosomeReportService chromosomeReportService;
	@Autowired
	private OverviewService overviewService;

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

		HPPChromosomeReportTXTWriter writer = new HPPChromosomeReportTXTWriter(os, overviewService);

		writer.write(chromosomeReportService.reportChromosome(chromosome));
	}
}
