package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.EntryReport;

import java.util.List;

public interface ChromosomeReportService {

	/**
	 * Get the list of gene/neXtProt entries informations found on the given chromosome
	 * @param chromosome the chromosome to get report
	 */
	List<EntryReport> exportChromosomeEntryReport(String chromosome);
}
