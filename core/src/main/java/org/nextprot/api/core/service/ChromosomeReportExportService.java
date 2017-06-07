package org.nextprot.api.core.service;

import org.nextprot.api.core.service.export.format.NextprotMediaType;

import java.io.IOException;
import java.io.OutputStream;

public interface ChromosomeReportExportService {

	/**
	 * Export the list of gene/neXtProt entries informations found on the given chromosome
	 * @param chromosome the chromosome to get report
	 * @param nextprotMediaType the export file format
	 * @param os the output stream to write into
	 */
	void exportChromosomeEntryReport(String chromosome, NextprotMediaType nextprotMediaType, OutputStream os) throws IOException;

	/**
	 * Export the list of neXtProt entries informations found on the given chromosome by accession
	 * @param chromosome the chromosome to get report
	 * @param nextprotMediaType the export file format
	 * @param os the output stream to write into
	 */
	void exportHPPChromosomeEntryReport(String chromosome, NextprotMediaType nextprotMediaType, OutputStream os) throws IOException;

	/**
	 * Export the neXtProt entry count by protein existence on the given chromosome by accession
	 * @param os the output stream to write into
	 */
	void exportHPPChromosomeEntryReportCountByProteinExistence(OutputStream os) throws IOException;
}
