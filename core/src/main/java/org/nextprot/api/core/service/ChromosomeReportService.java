package org.nextprot.api.core.service;

import org.nextprot.api.core.service.export.format.FileFormat;

import javax.servlet.http.HttpServletResponse;

public interface ChromosomeReportService {

	/**
	 * Export the list of gene/neXtProt entries informations found on the given chromosome
	 * @param chromosome the chromosome to get report
	 * @param fileFormat the export file format
	 * @param response the http response
	 */
	void exportChromosomeEntryReport(String chromosome, FileFormat fileFormat, HttpServletResponse response);
}
