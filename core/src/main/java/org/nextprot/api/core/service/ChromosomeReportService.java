package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.ChromosomeReport;

public interface ChromosomeReportService {

	/**
	 * Report chromosome entries
	 * @param chromosome the chromosome to get report
	 * @return a pojo containing the report
	 */
	ChromosomeReport reportChromosome(String chromosome);
}
