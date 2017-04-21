package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.ChromosomeReport;

public interface ChromosomeReportService {

	/**
	 * Report informations about neXtProt entries and genes located on the given chromosome
	 * @param chromosome the chromosome to get report
	 * @return a pojo containing the report
	 */
	ChromosomeReport reportChromosome(String chromosome);
}
